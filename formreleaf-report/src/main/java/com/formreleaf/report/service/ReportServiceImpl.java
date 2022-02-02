package com.formreleaf.report.service;

import com.formreleaf.common.dto.*;
import com.formreleaf.common.forms.*;
import com.formreleaf.common.utils.*;
import com.formreleaf.common.utils.tuple.Tuple2;
import com.formreleaf.domain.*;
import com.formreleaf.domain.enums.ProgramStatus;
import com.formreleaf.domain.enums.PublishStatus;
import com.formreleaf.domain.enums.RegistrationApproval;
import com.formreleaf.domain.enums.RegistrationStatus;
import com.formreleaf.domain.enums.ReportFormat;
import com.formreleaf.repository.*;
import com.itextpdf.text.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Bazlur Rahman Rokon
 * @since 9/28/15.
 */
@Service
@Transactional
public class ReportServiceImpl implements ReportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);

    private static final Font FONT_TITLE_NORMAL = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.DARK_GRAY);
    private static final Font FONT_TITLE_BOLD = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font FONT_VALUE_NORMAL = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
    private static final Font FONT_VALUE_BOLD = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);

    private static final String PROGRAM_KEY = "General - " + GeneralField.PROGRAM_NAME.getName();
    private static final String REGISTRATION_DATE_KEY = "General - " + GeneralField.REGISTRATION_DATE.getName();
    private static final String REGISTRATION_STATUS_KEY = "General - " + GeneralField.REGISTRATION_STATUS.getName();
    private static final String APPROVAL_KEY = "General - " + GeneralField.APPROVAL.getName();
    private static final String SECTION_KEY = "General - " + GeneralField.SECTION_NAME.getName();

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private FormProcessor processor;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private AgreementRepository agreementRepository;

    @Autowired
    private Environment env;

    @Autowired
    private ReportShareScheduleRepository reportShareScheduleRepository;

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    public Report save(ReportDefinitionDto reportDto, Organization organization) {
        Report report;

        if (reportDto.getId() != null) {
            report = reportRepository.findOne(reportDto.getId());
            convertTo(reportDto, report);
        } else {
            report = new Report();
            report.setOrganization(organization);
            convertTo(reportDto, report);
        }

        return reportRepository.save(report);
    }

    private void convertTo(ReportDefinitionDto reportDto, Report report) {
        report.setName(reportDto.getName());
        report.setFormData(processor.processReportDefinition(reportDto));
    }

    @Override
    public Report findById(Long id) {

        return reportRepository.findOne(id);
    }

    public ReportDefinitionDto getReportDefinition(Long reportId, User currentLoggedInUser) {
        if (reportId != null) {
            Report one = reportRepository.findOne(reportId);

            ReportDefinitionDto reportDefinitionDto = processor.processReportDefinition(one.getFormData());
            reportDefinitionDto.setId(one.getId());

            return reportDefinitionDto;

        } else {
            final List<Organization> organizations = organizationRepository.findOrganizationByUser(currentLoggedInUser.getId());

            if (organizations == null || organizations.size() <= 0) {
                throw new RuntimeException("Organization not found");
            } else {
                final Organization organization = organizations.get(0);
                List<BlockDto> allSelectableBlocks = findAllSelectableBlocks(organization);

                List<ApprovalDto> approvalDtos = new ArrayList<ApprovalDto>() {{
                    add(new ApprovalDto().setName(RegistrationApproval.APPROVED.name()));
                    add(new ApprovalDto().setName(RegistrationApproval.NOT_APPROVED.name()));
                }};

                List<ProgramDto> allSelectableProgram = findAllSelectableProgram(currentLoggedInUser);

                ReportDefinitionDto reportFilterDto = new ReportDefinitionDto();
                reportFilterDto.setApprovalDtos(approvalDtos);
                reportFilterDto.setSelectedBlocks(allSelectableBlocks);
                reportFilterDto.setPrograms(allSelectableProgram);

                return reportFilterDto;
            }
        }
    }

    public List<ProgramDto> findAllSelectableProgram(User currentLoggedInUser) {

        Organization organization = currentLoggedInUser.getOrganization();

        return organization.getPrograms()
                .stream()
                .filter(program -> !program.getDeleted()
                        && program.getPublish().getPublishStatus() == PublishStatus.PUBLIC
                        && (program.getPublish().getProgramStatus() != ProgramStatus.DRAFT))
                .map(program -> {
                    List<SectionDto> sectionDtos = program.getSections()
                            .stream()
                            .map(section -> new SectionDto().setId(section.getId()).setName(section.getName()))
                            .sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
                            .collect(Collectors.toList());

                    return new ProgramDto().setId(program.getId()).setName(program.getName()).setSectionDtos(sectionDtos);
                }).sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
                .collect(Collectors.toList());
    }

    public List<BlockDto> findAllSelectableBlocks(Organization organization) {

        List<BlockDto> allSelectableBlocks = findAllSelectableBlocksRemovingDuplicates(organization);
        allSelectableBlocks.add(0, getGeneralBlock()); // add general Block

        allSelectableBlocks.add(1, getAgreementBlock(organization));
        allSelectableBlocks.add(2, getPolicyBlock(organization));

        List<BlockDto> blockDtos = removeDuplicateBlock(allSelectableBlocks);
        final long[] blockIndex = {0};
        final long[] fieldIndex = {0};

        blockDtos.stream().sequential().forEach(blockDto -> {
            blockDto.setIndex(blockIndex[0]++);
            blockDto.getFieldDtos().stream().sequential()
                    .forEach(fieldDto -> fieldDto.setIndex(fieldIndex[0]++));
        });

        return blockDtos;
    }

    private List<BlockDto> findAllSelectableBlocksRemovingDuplicates(Organization organization) {

        List<BlockDto> blockDtoList = organization.getPrograms()
                .stream()
                .sequential()
                .filter(program -> !program.isDeleted()
                        && program.getPublish().getPublishStatus() == PublishStatus.PUBLIC
                        && (program.getPublish().getProgramStatus() != ProgramStatus.DRAFT))
                .map(Program::getFormTemplate)
                .map(processor::processProgramForm)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(form -> form.getSections()
                        .stream()
                        .sequential()
                        .map(section -> section.getBlocks()
                                .stream()
                                .sequential()
                                .filter(this::filterBlock)
                                .map(block -> convertBlockToDto(block, section.getTitle()))
                                .collect(Collectors.toList()))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return filterDuplicateFields(blockDtoList);
    }

    private List<BlockDto> filterDuplicateFields(List<BlockDto> blockDtoList) {
        Map<String, BlockDto> blockDtoMap = new LinkedHashMap<>();

        blockDtoList.forEach(blockDto -> {
            if (blockDtoMap.containsKey(blockDto.getBlockName())) {
                BlockDto block = blockDtoMap.get(blockDto.getBlockName());
                block.getFieldDtos().addAll(blockDto.getFieldDtos());
                blockDtoMap.put(blockDto.getBlockName(), block);
            } else {
                blockDtoMap.put(blockDto.getBlockName(), blockDto);
            }
        });

        return blockDtoMap.entrySet()
                .stream()
                .sequential()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    private boolean filterBlock(Block block) {

        return block.getFields().stream()
                .filter(Field::getSelected)
                .collect(Collectors.toList()).size() > 0;
    }

    private BlockDto convertBlockToDto(Block block, String sectionName) {
        BlockDto blockDto = new BlockDto();
        blockDto.setBlockName(block.getTitle());
        blockDto.setSectionName(sectionName);
        blockDto.setBlockDtoType(BlockDtoType.FIELD);

        if (block instanceof MultipleAllowedBlock) {
            MultipleAllowedBlock multipleAllowedBlock = (MultipleAllowedBlock) block;

            Set<FieldDto> fieldDtos = multipleAllowedBlock.getMultiFields().stream().sequential()
                    .map(fields -> fields.stream()
                            .sequential()
                            .map(field -> new FieldDto(field.getId(), field.getTitle()))
                            .collect(Collectors.toList()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            blockDto.setFieldDtos(fieldDtos);
        } else {
            Set<FieldDto> fieldDtos = block.getFields()
                    .stream()
                    .sequential()
                    .map(field -> new FieldDto(field.getId(), field.getTitle()))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            blockDto.setFieldDtos(fieldDtos);
        }

        return blockDto;
    }

    private List<BlockDto> removeDuplicateBlock(List<BlockDto> allSelectableFields) {
        List<BlockDto> blockDtos = new ArrayList<>();

        allSelectableFields.forEach(blockDto -> {
            if (blockDtos.contains(blockDto)) {
                BlockDto blockDtoIn = blockDtos.get(blockDtos.indexOf(blockDto));
                blockDtoIn.getFieldDtos().addAll(removeDuplicateFields(blockDto.getFieldDtos()));
            } else {
                blockDtos.add(blockDto);
            }
        });

        return blockDtos;
    }

    private List<FieldDto> removeDuplicateFields(Set<FieldDto> selectedFieldDtos) {

        final Set<FieldDto> setToReturn = new LinkedHashSet<>();
        final Set<FieldDto> test = new LinkedHashSet<>();

        setToReturn.addAll(selectedFieldDtos
                .stream()
                .sequential()
                .filter(fieldDto -> !test.add(fieldDto))
                .collect(Collectors.toList()));

        return new ArrayList<>(setToReturn);
    }

    //TODO: transform to SQL query to filter out the report for optimization
    @Override
    public List<Report> findReportsByOrganization(Organization organization) {
        List<Report> reports = reportRepository.findByOrganizationAndDeletedFalseOrderByNameAsc(organization);

        return reports.stream()
                .map(report -> report.setRegistrationCount(countRegistration(report)))
                .collect(Collectors.toList());
    }

    private long countRegistration(Report report) {

        return findAllRegistrations(report).size();
    }

    /**
     * Filter Criteria
     * Condition: 1. Filter out open programs
     * Condition: 2. Filter out programs that are included in the ReportDefinition
     * Condition: 3  Filter out registration by registration status if `registrantStatus` is 'COMPLETED' in the ReportDefinition
     * Condition: 4. Filter out registration by approval, if approval selected, filter by them, otherwise include all
     * Condition: 5. Filter out registration by registration date if `registrationDate` not null in the report Definition
     * Condition: 6. Filter out registration by registration name if `registrantName` is not null in the report definition
     * Condition: 7  Filter out registration by section if that are in the ReportDefinition
     */
    private List<Registration> findAllRegistrations(Report report) {

        ReportDefinitionDto reportDefinitionDto = processor.processReportDefinition(report.getFormData());

        List<Long> programIds = reportDefinitionDto.getPrograms()
                .stream()
                .filter((programDto) -> programDto.isSelected() || programDto.getSectionDtos().stream().anyMatch(SectionDto::isSelected))
                .mapToLong(ProgramDto::getId)
                .boxed()
                .collect(Collectors.toList());

        List<String> approvalList = reportDefinitionDto.getApprovalDtos()
                .stream()
                .filter(ApprovalDto::isSelected)
                .map(ApprovalDto::getName)
                .collect(Collectors.toList());

        Set<Program> programs = report.getOrganization().getPrograms();

        return programs.stream()
                .filter(program -> program.getPublish().getProgramStatus() == ProgramStatus.OPEN || program.getPublish().getProgramStatus() == ProgramStatus.CLOSED) //Condition: 1
                .filter(program -> filterByProgram(programIds, program)) //Condition: 2
                .map(Program::getRegistrations)
                .map(registrations -> registrations
                                .stream()
                                .filter(registration1 -> registration1.getRegistrationStatus() == RegistrationStatus.COMPLETED) //Condition: 3
                                .filter(registration -> filterApproval(approvalList, registration))//Condition: 4
                                .filter(registration -> filterByRegistrationDate(reportDefinitionDto, registration)) //Condition: 5
                                .filter(registration -> filterByRegistrantName(reportDefinitionDto.getRegistrantName(), registration)) //Condition: 6
                                .filter(registration -> filterBySections(reportDefinitionDto, registration)) // Condition: 7
                                .collect(Collectors.toList())
                ).flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private boolean filterApproval(List<String> approvalList, Registration registration) {

        return CollectionUtils.isEmpty(approvalList) || approvalList
                .stream()
                .anyMatch(approval -> RegistrationApproval.valueOf(approval) == registration.getRegistrationApproval());
    }

    private boolean filterBySections(ReportDefinitionDto reportDefinitionDto, Registration registration) {
        Program program = registration.getProgram();
        Set<com.formreleaf.domain.Section> sections = registration.getSections();

        List<ProgramDto> programDtos = reportDefinitionDto.getPrograms()
                .stream()
                .filter((programDto) -> programDto.isSelected() || programDto.getSectionDtos().stream().anyMatch(SectionDto::isSelected))
                .collect(Collectors.toList());

        return CollectionUtils.isEmpty(programDtos) || programDtos.stream().anyMatch(programDto -> {
            boolean programEquals = programDto.getId().equals(program.getId());
            List<SectionDto> sectionDtos = programDto.getSectionDtos().stream().filter(SectionDto::isSelected).collect(Collectors.toList());
            if (programEquals && sectionDtos.size() == 0) {
                return true;
            } else {
                List<Long> sectionDtoIds = sectionDtos.stream().mapToLong(SectionDto::getId).boxed().collect(Collectors.toList());
                List<Long> sectionIds = sections.stream().mapToLong(com.formreleaf.domain.Section::getId).boxed().collect(Collectors.toList());
                return CollectionUtils.containsAny(sectionDtoIds, sectionIds);
            }
        });
    }

    private boolean filterByProgram(List<Long> programIds, Program program) {
        //if programId List is empty, we really don't need filter anything, return true
        return CollectionUtils.isEmpty(programIds) || programIds.stream().anyMatch(aLong -> aLong.equals(program.getId()));
    }

    private boolean filterByRegistrantName(String registrantName, Registration registration) {

        if (StringUtils.isNotEmpty(registrantName)) {
            String names[] = {registrantName};

            if (registrantName.contains(",")) {
                names = registrantName.split(",");
            }

            boolean matchFound = Arrays.asList(names)
                    .stream()
                    .anyMatch(s -> org.apache.commons.lang3.StringUtils.containsIgnoreCase(registration.getRegistrantName(), s.trim()));

            if (matchFound) {
                LOGGER.info("registrantName : {}, registration.getRegistrantName() : {} are equal", registrantName, registration.getRegistrantName());
            }
            return matchFound;
        }

        return true;
    }

    private boolean filterByRegistrationDate(ReportDefinitionDto reportDefinitionDto, Registration registration) {

        if (reportDefinitionDto.getRegistrationDate() == null ||
                (reportDefinitionDto.getRegistrationDate().getStartDate() == null
                        || reportDefinitionDto.getRegistrationDate().getEndDate() == null)) {

            return true; // if this fields are null, we don't need to filter with anything, thus, return true
        } else {
            return ((registration.getRegistrationDate().after(reportDefinitionDto.getRegistrationDate().getStartDate())
                    || registration.getRegistrationDate().equals(reportDefinitionDto.getRegistrationDate().getStartDate()))
                    && (registration.getRegistrationDate().before(reportDefinitionDto.getRegistrationDate().getEndDate())
                    || registration.getRegistrationDate().equals(reportDefinitionDto.getRegistrationDate().getEndDate())));
        }
    }

    @Override
    public void delete(Long id) {

        reportRepository.delete(id);
    }

    @Override
    public ResponseEntity<?> downloadCsv(Long reportId) {
        Report one = reportRepository.findOne(reportId);

        return generateCsvData(one);
    }

    @Override
    public ResponseEntity<?> downloadPdf(Long reportId) {
        Report one = reportRepository.findOne(reportId);

        return generatePdf(one);
    }

    private ResponseEntity<?> generatePdf(Report report) {

        String reportName = report.getName() + "_Report.pdf";
        reportName = reportName.replaceAll("\\s", "_");

        try {
            byte[] pdfContentAsByte = getPdfContentAsByte(report);

            return getPdfResponseEntity(pdfContentAsByte, reportName);
        } catch (DocumentException | IOException e) {
            LOGGER.error("Unable to build pdf", e);

            return ResponseEntity.badRequest().build();
        }
    }

    private void printSinglePage(List<BlockDto> selectedBlocks, List<String> selectedFieldIds, Document document, PdfWriter pdfWriter, Registration registration, Form form) {
        LinkedHashSet<com.formreleaf.common.forms.Section> selectedSections = getSelectedSections(selectedFieldIds, form);

        try {
            printReportTitle(registration.getRegistrantName(), document);
            printSectionTitle("Section: " + registration.getSectionNames(), document);

            document.add(Chunk.NEWLINE);

            for (com.formreleaf.common.forms.Section section : selectedSections) {
                List<Block> blocks = section.getBlocks();

                if (!blocks.isEmpty()) {
                    printSection(section, document);
                }
            }

            List<BlockDto> blockDtoExtras = selectedBlocks.stream()
                    .filter(blockDto -> blockDto.getBlockDtoType() == BlockDtoType.POLICY
                            || blockDto.getBlockDtoType() == BlockDtoType.AGREEMENT)
                    .collect(Collectors.toList());

            printPolicy(document, pdfWriter, registration, blockDtoExtras);
            printAgreement(document, pdfWriter, registration, blockDtoExtras);

        } catch (DocumentException e) {
            LOGGER.info("Unable to write document", e);
        }
    }

    private void printAgreement(Document document, PdfWriter pdfWriter, Registration registration, List<BlockDto> blockDtoExtras) throws DocumentException {
        List<FieldDto> agreementsDtoExtras = getFields(blockDtoExtras, BlockDtoType.AGREEMENT);

        if (CollectionUtils.isNotEmpty(agreementsDtoExtras)) {
            Set<Signature> signatures = registration.getSignatures();

            printSectionTitle("Agreement", document);
            agreementsDtoExtras.forEach(fieldDto -> {
                if (fieldDto.getFieldDtoType() == FieldDtoType.TEXT) {
                    Optional<Agreement> agreementToPrint = signatures.stream().map(Signature::getAgreement)
                            .filter(agreement -> agreement.getId().equals(fieldDto.getFieldId()))
                            .findFirst();
                    agreementToPrint.ifPresent(agreement -> printAgreementText(agreement, pdfWriter, document));

                } else if (fieldDto.getFieldDtoType() == FieldDtoType.SIGN_OFF) {
                    Optional<Signature> signatureToPrint = signatures.stream()
                            .filter(signature -> signature.getAgreement().getId().equals(fieldDto.getFieldId()))
                            .findFirst();
                    signatureToPrint.ifPresent(signature -> printSignature(signature, registration.getRegistrant().getFullName(), document));
                }
            });
        }
    }

    private void printPolicy(Document document, PdfWriter pdfWriter, Registration registration, List<BlockDto> blockDtoExtras) throws DocumentException {
        List<FieldDto> policiesDtoExtras = getFields(blockDtoExtras, BlockDtoType.POLICY);

        if (CollectionUtils.isNotEmpty(policiesDtoExtras)) {
            Set<Policy> policies = registration.getProgram().getPolicies();

            printSectionTitle("Policy", document);
            policiesDtoExtras.forEach(fieldDto -> {
                Optional<Policy> policyToPrint = policies.stream().filter(policy -> policy.getId().equals(fieldDto.getFieldId())).findFirst();
                policyToPrint.ifPresent(policy -> printPolicy(policy, pdfWriter, document));
            });
        }
    }

    private void printSignature(Signature signature, String signeesName, Document document) {
        try {
            printSectionTitle(signature.getAgreement().getTitle() + " : Sign Off", document);

            byte[] bytes = ImageUtils.convert(signature.getSign());
            Image image = Image.getInstance(bytes, true);
            image.scaleToFit(600, 75);
            image.setAlignment(Element.ALIGN_LEFT);
            document.add(image);

            String date = simpleDateFormat.format(signature.getLastModifiedDate());

            Paragraph signOff = new Paragraph(signeesName + ", " + date, FONT_TITLE_NORMAL);
            document.add(signOff);
        } catch (DocumentException | IOException e) {
            LOGGER.info("Unable print signature", e);
        }
    }

    private List<FieldDto> getFields(List<BlockDto> blockDtos, BlockDtoType blockDtoType) {

        return blockDtos.stream().filter(blockDto -> blockDto.getBlockDtoType() == blockDtoType)
                .map(BlockDto::getFieldDtos)
                .flatMap(Collection::stream)
                .filter(FieldDto::isSelected)
                .collect(Collectors.toList());
    }

    private void printPolicy(Policy policy, PdfWriter pdfWriter, Document document) {
        try {
            printSectionTitle(policy.getTitle() + " : Text", document);
            String fontStyle = "<div style=\"font-family: Helvetica, Arial, sans-serif; font-size: 10px\">";
            String agreement = fontStyle + policy.getDetails() + "</div>";

            StringReader stringReader = new StringReader(agreement);

            try {
                XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, document, stringReader);
            } catch (IOException e) {
                LOGGER.info("Unable to parse html", e);
            }

            document.add(Chunk.NEWLINE);

        } catch (Exception e) {
            LOGGER.info("Unable to write policy");
        }
    }

    private void printAgreementText(Agreement agreement, PdfWriter pdfWriter, Document document) {
        try {
            printSectionTitle(agreement.getTitle() + " : Text", document);
            String fontStyle = "<div style=\"font-family: Helvetica, Arial, sans-serif; font-size: 10px\">";
            String text = fontStyle + agreement.getDetails() + "</div>";

            StringReader stringReader = new StringReader(text);

            try {
                XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, document, stringReader);
            } catch (IOException e) {
                LOGGER.info("Unable to parse html", e);
            }

            document.add(Chunk.NEWLINE);

        } catch (DocumentException e) {
            LOGGER.info("Unable to write ");
        }
    }

    private void printSection(com.formreleaf.common.forms.Section section, Document document) throws DocumentException {
        List<Block> blocks = section.getBlocks().stream().filter(block -> !block.getFields().isEmpty()).collect(Collectors.toList());

        if (!blocks.isEmpty()) {
            printSectionTitle(section.getTitle(), document);
            int size = blocks.size();
            int divide = size / 3;
            int reminder = size % 3;

            int lastIndex = 0;
            for (int i = 0; i < divide; i++) {
                List<Block> subList = blocks.subList(lastIndex, lastIndex = (i + 3));
                printPartialTable(document, subList);
            }
            if (reminder > 0) {
                List<Block> subList = blocks.subList(lastIndex, lastIndex + reminder);
                printPartialTable(document, subList);
            }
        }
    }

    private void printPartialTable(Document document, List<Block> subList) throws DocumentException {
        PdfPTable mainTable = new PdfPTable(subList.size());
        mainTable.getDefaultCell().setBorderWidth(0f);
        mainTable.setHorizontalAlignment(Element.ALIGN_LEFT);

        subList.stream().filter(block -> !block.getFields().isEmpty()).forEach(block -> {
            PdfPTable pdfTable = createPdfTable(block);
            pdfTable.getDefaultCell().setBorderWidth(0f);
            pdfTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            mainTable.addCell(pdfTable);
        });
        document.add(mainTable);
        document.add(Chunk.NEWLINE);
    }

    private List<String> getSelectedFieldIds(List<BlockDto> selectedBlocks) {

        return selectedBlocks.stream()
                .map(blockDto -> blockDto.getFieldDtos()
                        .stream()
                        .filter(FieldDto::isSelected)
                        .map(FieldDto::getId).collect(Collectors.toList()))
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    private ResponseEntity getPdfResponseEntity(byte[] bytes, String reportName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.parseMediaType("application/pdf"));
        httpHeaders.set("Content-Disposition", "attachment; filename=" + reportName);
        httpHeaders.setContentLength(bytes.length);

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }

    private void printSectionTitle(String title, Document document) throws DocumentException {
        Paragraph sectionTitle = new Paragraph((title).toUpperCase());
        sectionTitle.setFont(FONT_TITLE_NORMAL);
        sectionTitle.setSpacingBefore(2.5f);
        sectionTitle.setSpacingAfter(2.5f);
        document.add(sectionTitle);
    }

    private void printReportTitle(String registrationName, Document document) throws DocumentException {
        String fullName;

        try {
            // interchange first name and last name
            String[] names = registrationName.split(" ");
            String lastName = names[names.length - 1];

            String firstNames[] = new String[names.length - 1];
            System.arraycopy(names, 0, firstNames, 0, names.length - 1);
            fullName = lastName + " " + String.join(" ", firstNames);
        } catch (Exception e) {
            LOGGER.error("Unable to split names", e);
            fullName = registrationName;
        }

        String reportTitle = ("Registration # " + fullName).toUpperCase();
        Paragraph title = new Paragraph(reportTitle, FONT_TITLE_BOLD);
        title.setAlignment(Element.ALIGN_RIGHT);
        document.add(title);
        document.add(Chunk.NEWLINE);
    }

    private PdfPTable createPdfTable(Block block) {
        PdfPTable table = new PdfPTable(1);
        table.getDefaultCell().setBorderWidth(0f);

        Chunk underline = new Chunk(block.getTitle(), FONT_TITLE_BOLD);
        underline.setUnderline(0.1f, -2f);
        Phrase phrase = new Phrase(underline);

        PdfPCell blockCell = new PdfPCell(phrase);
        blockCell.setBorderWidth(0f);

        table.addCell(blockCell);

        List<Field> fields = getFields(block);
        PdfPTable contents = new PdfPTable(1);
        contents.getDefaultCell().setBorderWidth(0f);

        Paragraph paragraph = new Paragraph();

        for (Field field : fields) {
            String item = field.getTitle() + ":    ";
            String value = getFieldValue(field);
            if (StringUtils.isNotEmpty(value)) {
                item += value;
            }
            paragraph.add(new Phrase(item, FONT_TITLE_NORMAL));
            paragraph.add(Chunk.NEWLINE);
        }

        table.addCell(paragraph);

        return table;
    }

    private List<Field> getFields(Block block) {
        List<Field> fields;
        if (block instanceof MultipleAllowedBlock) {
            MultipleAllowedBlock multipleAllowedBlock = (MultipleAllowedBlock) block;
            fields = multipleAllowedBlock.getMultiFields()
                    .stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        } else {
            fields = block.getFields();
        }
        return fields;
    }

    private String getFieldValue(Field field) {
        String value;
        if (field instanceof ConcernField) {
            ConcernField concernField = (ConcernField) field;

            if (concernField.isConcernedYes()) {
                value = concernField.getValue();
            } else {
                value = "None";
            }
        } else {
            value = field.getValue();
        }

        if (field.getFieldType() == FieldType.DATE) {
            value = DateUtils.convert(value);
        }
        return value;
    }

    private LinkedHashSet<com.formreleaf.common.forms.Section> getSelectedSections(List<String> selectedFieldIds, Form form) {

        return form.getSections().stream().sequential().map(section -> {
            //selectedBlocks
            List<Block> collect = section.getBlocks().stream()
                    .filter(block -> block.getFields().stream()
                            .anyMatch(Field::isSelected)).collect(Collectors.toList());

            List<Block> filteredBlocks = collect.stream().map(block -> {
                block.setFields(block.getFields()
                        .stream()
                        .filter(field -> selectedFieldIds.contains(field.getId())).collect(Collectors.toList()));

                return block;
            }).collect(Collectors.toList());

            section.setBlocks(filteredBlocks);

            return section;
        }).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private ResponseEntity<?> generateCsvData(Report report) {
        byte[] bytes = getCsvContentAsByte(report);

        return getCsvResponseEntity(report, bytes);
    }

    private void sortByLastName(List<String> csvHeaders, List<LinkedHashMap<String, String>> mapList) {
        String firstName = "Participant Information - Personal - First Name";
        String lastName = "Participant Information - Personal - Last Name";

        int firstNameIndex = csvHeaders.indexOf(firstName);
        int lastNameIndex = csvHeaders.indexOf(lastName);

        if (firstNameIndex != -1 && lastNameIndex != -1) {
            csvHeaders.remove(lastNameIndex);
            csvHeaders.add(firstNameIndex, lastName);
        }

        if (lastNameIndex != -1) {
            mapList.sort((o1, o2) -> o1.get(lastName).compareToIgnoreCase(o2.get(lastName)));
        }
    }

    private ResponseEntity<?> getCsvResponseEntity(Report report, byte[] bytes) {
        if (bytes.length == 0) {

            return ResponseEntity.badRequest().body("Couldn't generate CSV report!");
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.parseMediaType("text/csv"));
        String reportName = report.getName().replaceAll("\\s", "_");
        httpHeaders.set("Content-Disposition", "attachment; filename=" + reportName + ".csv");
        httpHeaders.setContentLength(bytes.length);

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }

    private byte[] getCsvContents(List<LinkedHashMap<String, String>> mapList, String[] headers, CellProcessor[] cellProcessors) {
        ICsvMapWriter mapWriter = null;

        ByteArrayOutputStream fileContent = new ByteArrayOutputStream();

        try {
            mapWriter = new CsvMapWriter(new OutputStreamWriter(fileContent), CsvPreference.STANDARD_PREFERENCE);
            mapWriter.writeHeader(headers);

            final ICsvMapWriter finalMapWriter = mapWriter;
            mapList.forEach(map -> {
                try {
                    finalMapWriter.write(map, headers, cellProcessors);
                } catch (IOException e) {
                    LOGGER.error("Unable to write csv", e);
                }
            });

        } catch (IOException e) {
            LOGGER.error("Unable to process", e);
        } finally {
            if (mapWriter != null) {
                try {
                    mapWriter.close();
                } catch (IOException e) {
                    LOGGER.error("Unable to close mapWriter", e);
                }
            }
        }

        return fileContent.toByteArray();
    }

    private CellProcessor[] getCellProcessors(String[] headers) {
        List<org.supercsv.cellprocessor.Optional> processors = IntStream.range(0, headers.length)
                .mapToObj(i -> new org.supercsv.cellprocessor.Optional()).collect(Collectors.toList());

        return processors.toArray(new CellProcessor[processors.size()]);
    }

    private List<LinkedHashMap<String, String>> getCsvValueMapList(Set<String> headerList, List<BlockDto> selectedBlocks, List<Registration> allRegistrations) {
        List<LinkedHashMap<String, String>> mapList = new ArrayList<>();

        List<BlockDto> policyBlock = selectedBlocks.stream().filter(blockDto -> blockDto.getBlockDtoType() == BlockDtoType.POLICY)
                .sorted((o1, o2) -> o1.getIndex().compareTo(o2.getIndex()))
                .collect(Collectors.toList());

        List<BlockDto> agreementBlock = selectedBlocks.stream().filter(blockDto -> blockDto.getBlockDtoType() == BlockDtoType.AGREEMENT)
                .sorted((o1, o2) -> o1.getIndex().compareTo(o2.getIndex()))
                .collect(Collectors.toList());

        for (Registration registration : allRegistrations) {
            LinkedHashMap<String, String> fieldsMap = processor.process(registration.getFormData())
                    .map(this::getFieldsMap)
                    .orElseGet(LinkedHashMap::new);
            addGeneralFields(headerList, registration, fieldsMap);
            addPolicyFields(headerList, policyBlock, registration, fieldsMap);
            addAgreementFields(headerList, agreementBlock, registration, fieldsMap);

            mapList.add(fieldsMap);
        }

        return mapList;
    }

    private void addAgreementFields(Set<String> headerList, List<BlockDto> agreementBlock, Registration registration, LinkedHashMap<String, String> fieldsMap) {
        List<FieldDto> agreementFields = agreementBlock
                .stream()
                .map(BlockDto::getFieldDtos)
                .flatMap(Collection::stream)
                .filter(FieldDto::isSelected)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(agreementFields)) {
            Set<Signature> signatures = registration.getSignatures();
            for (FieldDto fieldDto : agreementFields) {
                if (fieldDto.getFieldDtoType() == FieldDtoType.TEXT) {
                    Optional<Agreement> agreementToPrint = signatures.stream().map(Signature::getAgreement)
                            .filter(agreement -> agreement.getId().equals(fieldDto.getFieldId()))
                            .findFirst();
                    agreementToPrint.ifPresent(agreement -> {
                        String key = "Agreement - " + agreement.getTitle().trim() + " - Text";
                        String value = agreement.getDetails();
                        headerList.add(key);
                        fieldsMap.put(key, value);
                    });

                } else if (fieldDto.getFieldDtoType() == FieldDtoType.SIGN_OFF) {
                    Optional<Signature> signatureToPrint = signatures.stream()
                            .filter(signature -> signature.getAgreement().getId().equals(fieldDto.getFieldId()))
                            .findFirst();
                    signatureToPrint.ifPresent(signature -> {
                        String key = "Agreement - " + signature.getAgreement().getTitle().trim() + " - SignOff";
                        String value = getSignatureUrl(signature);
                        headerList.add(key);
                        fieldsMap.put(key, value);
                    });
                }
            }
        }
    }

    private void addPolicyFields(Set<String> headerList, List<BlockDto> policyBlock, Registration registration, LinkedHashMap<String, String> fieldsMap) {
        List<FieldDto> policyFields = policyBlock
                .stream()
                .map(BlockDto::getFieldDtos)
                .flatMap(Collection::stream)
                .filter(FieldDto::isSelected)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(policyFields)) {
            for (FieldDto fieldDto : policyFields) {

                Optional<Policy> first = registration.getProgram().getPolicies().stream().filter(policy -> policy.getId().equals(fieldDto.getFieldId())).findFirst();
                first.ifPresent(policy -> {
                    String key = "Policy - " + policy.getTitle().trim() + " - Text";
                    String value = policy.getDetails();

                    headerList.add(key);
                    fieldsMap.put(key, value);
                });
            }
        }
    }

    private LinkedHashSet<String> getHeaders(List<BlockDto> selectedBlocks) {
        LOGGER.info("selectedBlocks -> size : {}", selectedBlocks.size());

        return selectedBlocks
                .stream()
                .map(this::getHeaders)
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private LinkedHashSet<String> getHeaders(BlockDto blocKDto) {

        return blocKDto.getFieldDtos()
                .stream()
                .sequential()
                .filter(FieldDto::isSelected)
                .sorted((o1, o2) -> o1.getIndex().compareTo(o2.getIndex()))
                .map(fieldDto -> makeJoinedCSVHeaderName(blocKDto.getSectionName(), blocKDto.getBlockName(), fieldDto.getName()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private LinkedHashMap<String, String> getFieldsMap(Form form) {

        return form.getSections()
                .stream()
                .sequential()
                .map(section -> section.getBlocks()
                        .stream()
                        .map(block -> getTupleList(block, section.getTitle()))
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(t1 -> t1._0, t2 -> t2._1, (v1, v2) -> null, LinkedHashMap::new));
    }

    private void addGeneralFields(Set<String> headerList, Registration registration, Map<String, String> map) {

        if (headerList.contains(PROGRAM_KEY)) {
            map.put(PROGRAM_KEY, registration.getProgram().getName());
        }

        if (headerList.contains(SECTION_KEY)) {
            map.put(SECTION_KEY, registration.getSectionNames());
        }

        if (headerList.contains(REGISTRATION_DATE_KEY)) {
            map.put(REGISTRATION_DATE_KEY, simpleDateFormat.format(registration.getRegistrationDate()));
        }

        if (headerList.contains(REGISTRATION_STATUS_KEY)) {
            if (registration.getRegistrationStatus() != null) {
                map.put(REGISTRATION_STATUS_KEY, registration.getRegistrationStatus().getLabel());
            }
        }

        if (headerList.contains(APPROVAL_KEY)) {
            if (registration.getRegistrationApproval() != null) {
                map.put(APPROVAL_KEY, registration.getRegistrationApproval().getLabel());
            }
        }
    }

    private synchronized String getSignatureUrl(Signature signature) {
        final String[] activeProfiles = env.getActiveProfiles();

        return ServletUtils.getDefaultContextURL("media/" + LongCipher.getInstance().encrypt(signature.getId()), activeProfiles[0]);
    }

    private List<Tuple2<String, String>> getTupleList(Block block, String sectionTitle) {
        if (block instanceof MultipleAllowedBlock) {
            MultipleAllowedBlock multipleAllowedBlock = (MultipleAllowedBlock) block;

            return multipleAllowedBlock.getMultiFields().stream().sequential()
                    .map(fields -> fields.stream()
                            .map(field -> mapValueToTuple(field, multipleAllowedBlock, sectionTitle))
                            .collect(Collectors.toList()))
                    .flatMap(Collection::stream).collect(Collectors.toList());
        } else {

            return block.getFields()
                    .stream()
                    .sequential()
                    .filter(Field::getSelected)
                    .map(field -> mapValueToTuple(field, block, sectionTitle)).collect(Collectors.toList());
        }
    }

    private Tuple2<String, String> mapValueToTuple(Field field, Block block, String sectionTitle) {

        if (field instanceof ConcernField) {
            ConcernField concernField = (ConcernField) field;
            String value;

            if (concernField.isConcernedYes()) {
                value = concernField.getValue();
            } else {
                value = "None";
            }
            field.setValue(value);
        }

        return getTuple(sectionTitle, block.getTitle(), field);
    }

    private Tuple2<String, String> getTuple(String sectionTitle, String blockName, Field field) {
        String toConvert = field.getValue();

        if (field.getFieldType() == FieldType.DATE) {
            toConvert = DateUtils.convert(toConvert);
        }

        return Tuple2.valueOf(makeJoinedCSVHeaderName(sectionTitle, blockName, field.getTitle()),
                Optional.ofNullable(toConvert).map(s -> s).orElseGet(String::new));
    }

    //blockName and title are required
    private String makeJoinedCSVHeaderName(String sectionName, String blockName, String title) {

        if (StringUtils.isEmpty(sectionName)) {
            return String.format("%s - %s", blockName, title).trim();
        }

        return String.format("%s - %s - %s", sectionName, blockName, title).trim();
    }

    public Set<BlockDto> findDuplicates(List<BlockDto> listContainingDuplicates) {

        final Set<BlockDto> setToReturn = new HashSet<>();
        final Set<BlockDto> set1 = new HashSet<>();

        setToReturn.addAll(listContainingDuplicates.stream().filter(yourInt -> !set1.add(yourInt)).collect(Collectors.toList()));

        return setToReturn;
    }

    /**
     * Custom block, that only add general items
     * that's only exception we had to make
     */
    private BlockDto getGeneralBlock() {
        final int index[] = {0};

        Set<FieldDto> generalFields = Arrays.asList(GeneralField.values())
                .stream()
                .sequential()
                .map(field -> new FieldDto("" + ++index[0], field.getName()))
                .sorted((o1, o2) -> o1.getId().compareTo(o2.getId()))
                .collect(Collectors.toSet());

        return new BlockDto()
                .setSectionName("")
                .setBlockDtoType(BlockDtoType.GENERAL)
                .setBlockName("General")
                .setFieldDtos(generalFields);
    }


    private BlockDto getAgreementBlock(Organization organization) {
        BlockDto blockDto = new BlockDto();
        blockDto.setBlockName("Agreement");
        blockDto.setSectionName("");
        blockDto.setBlockDtoType(BlockDtoType.AGREEMENT);

        final int index1[] = {0};
        final int index2[] = {0};

        final Set<Agreement> agreementSet = agreementRepository.findAllAgreementsByOrganization_IdAndDeletedFalse(organization.getId());
        List<Agreement> agreements = agreementSet.stream().filter(agreement1 -> !agreement1.isDeleted()).collect(Collectors.toList());
        agreements.forEach(agreement -> {

            String title = agreement.getTitle() + " - Text";
            ++index1[0];
            blockDto.getFieldDtos().add(new FieldDto("" + index1[0], title)
                    .setFieldId(agreement.getId())
                    .setFieldDtoType(FieldDtoType.TEXT)
                    .setIndex((long) index1[0]));

            title = agreement.getTitle() + " - Sign Off";
            blockDto.getFieldDtos().add(new FieldDto("" + ++index2[0], title)
                    .setFieldId(agreement.getId())
                    .setFieldDtoType(FieldDtoType.SIGN_OFF)
                    .setIndex((long) index1[0]));
        });

        return blockDto;
    }

    private BlockDto getPolicyBlock(Organization organization) {

        BlockDto blockDtoExtra = new BlockDto();
        blockDtoExtra.setBlockName("Policy");
        blockDtoExtra.setSectionName("");
        blockDtoExtra.setBlockDtoType(BlockDtoType.POLICY);

        final Long index[] = {(long) 0};

        final Set<Policy> policySet = policyRepository.findAllPolicyByOrganization_IdAndDeletedFalse(organization.getId());
        List<Policy> policies = policySet.stream().filter(policy -> !policy.isDeleted())
                .collect(Collectors.toList());

        policies.forEach(policy -> {
            String title = policy.getTitle() + " - Text";
            ++index[0];
            blockDtoExtra.getFieldDtos().add(new FieldDto("" + index[0], title)
                    .setFieldId(policy.getId())
                    .setFieldDtoType(FieldDtoType.TEXT)
                    .setIndex(index[0]));
        });

        return blockDtoExtra;
    }

    public Report findOne(Long reportId) {

        return reportRepository.findOne(reportId);
    }

    public ReportShareSchedule saveReportSchedule(ReportShareSchedule reportShareSchedule) {
        List<String> emails = Arrays.asList(reportShareSchedule.getEmails().split(",")).stream().collect(Collectors.toList());

        ReportShareSchedule shareSchedule = Optional.ofNullable(reportShareSchedule.getId())
                .map(id -> {
                    ReportShareSchedule shareScheduleFromDb = reportShareScheduleRepository.findOne(id);
                    shareScheduleFromDb.setEmails("NotEmpty");//transient, but added to prevent hibernate validation error TODO quick and dirty, revisit it please
                    shareScheduleFromDb.setRecipientEmails(emails);
                    shareScheduleFromDb.setReportSharingFrequency(reportShareSchedule.getReportSharingFrequency());
                    shareScheduleFromDb.setReportFormat(reportShareSchedule.getReportFormat());
                    shareScheduleFromDb.setStartDate(reportShareSchedule.getStartDate());
                    shareScheduleFromDb.setEndDate(reportShareSchedule.getEndDate());

                    return shareScheduleFromDb;
                }).orElseGet(() -> {
                    reportShareSchedule.setRecipientEmails(emails);
                    Report report = reportRepository.findOne(reportShareSchedule.getReport().getId());
                    reportShareSchedule.setReport(report);

                    return reportShareSchedule;
                });

        return reportShareScheduleRepository.save(shareSchedule);
    }

    @Override
    public List<ReportShareSchedule> findAllReportShareSchedule(Long reportId) {

        return reportRepository.findOne(reportId)
                .getReportShareSchedules().stream()
                .filter(reportShareSchedule -> !reportShareSchedule.isDeleted())
                .sorted((left, right) -> right.getCreatedDate().compareTo(left.getCreatedDate()))
                .collect(Collectors.toList());
    }

    @Override
    public ReportShareSchedule findReportShareSchedule(Long reportShareScheduleId) {

        return reportShareScheduleRepository.findOne(reportShareScheduleId);
    }

    @Override
    public ReportShareSchedule deleteReportShareSchedule(Long reportShareScheduleId) {
        ReportShareSchedule shareSchedule = reportShareScheduleRepository.findOne(reportShareScheduleId);
        shareSchedule.setEmails("NotEmpty"); //transient, but added to prevent hibernate validation error TODO quick and dirty, revisit it please
        shareSchedule.setDeleted(true);

        return reportShareScheduleRepository.save(shareSchedule);
    }

    @Override
    public List<Report> findAllReports() {

        return reportRepository.findAll();
    }

    @Override
    public void updateExpiredScheduler() {
        LocalDateTime now = LocalDateTime.now();
        List<ReportShareSchedule> reportShareScheduleList = reportShareScheduleRepository.findAllByDeletedFalseAndExpiredFalse()
                .stream()
                .filter(reportShareSchedule -> reportShareSchedule.getEndDate().isBefore(now))
                .map(reportShareSchedule -> {
                    reportShareSchedule.setExpired(true);
                    reportShareSchedule.setEmails("NotEmpty");// quick tweak

                    return reportShareSchedule;
                }).collect(Collectors.toList());

        LOGGER.info("[event:OFFLINE_REPORT] Total expired scheduler: {}", reportShareScheduleList.size());

        reportShareScheduleRepository.save(reportShareScheduleList);
    }

    private synchronized byte[] getCsvContentAsByte(Report report) {
        ReportDefinitionDto reportDefinitionDto = processor.processReportDefinition(report.getFormData());
        List<BlockDto> selectedBlocks = reportDefinitionDto.getSelectedBlocks();

        List<BlockDto> fieldBlocks = selectedBlocks
                .stream()
                .filter(blockDto -> blockDto.getBlockDtoType() == BlockDtoType.FIELD
                        || blockDto.getBlockDtoType() == BlockDtoType.GENERAL)
                .collect(Collectors.toList());
        fieldBlocks.sort((o1, o2) -> o1.getIndex().compareTo(o2.getIndex()));

        LinkedHashSet<String> headerList = getHeaders(fieldBlocks);

        List<Registration> allRegistrations = findAllRegistrations(report);
        List<LinkedHashMap<String, String>> mapList = getCsvValueMapList(headerList, selectedBlocks, allRegistrations);

        List<String> csvHeaders = new ArrayList<>(headerList);
        sortByLastName(csvHeaders, mapList);

        String[] headers = csvHeaders.toArray(new String[csvHeaders.size()]);
        CellProcessor[] cellProcessors = getCellProcessors(headers);

        return getCsvContents(mapList, headers, cellProcessors);
    }

    private synchronized byte[] getPdfContentAsByte(Report report) throws DocumentException, IOException {
        ReportDefinitionDto reportDefinitionDto = processor.processReportDefinition(report.getFormData());
        List<BlockDto> selectedBlocks = reportDefinitionDto.getSelectedBlocks();
        List<Registration> allRegistrations = findAllRegistrations(report);
        List<String> selectedFieldIds = getSelectedFieldIds(selectedBlocks);

        Document document = new Document(PageSize.A4);

        ByteArrayOutputStream pdfContent = new ByteArrayOutputStream();

        PdfWriter pdfWriter = PdfWriter.getInstance(document, pdfContent);
        document.open();

        Collections.sort(allRegistrations, (o1, o2) -> StringUtils.compareNullableString(o1.getLastName(), o2.getLastName(), true));

        allRegistrations.forEach(registration -> {
            Optional<Form> formOptional = processor.process(registration.getFormData());

            if (formOptional.isPresent()) {
                printSinglePage(selectedBlocks, selectedFieldIds, document, pdfWriter, registration, formOptional.get());
                document.newPage();
            }
        });

        pdfWriter.close();
        document.close();

        return pdfContent.toByteArray();
    }

    @Override
    public byte[] getCsvContent(Report report) {

        return getCsvContentAsByte(report);
    }

    @Override
    public byte[] getPdfContent(Report report) {

        try {
            return getPdfContentAsByte(report);

        } catch (DocumentException | IOException e) {
            LOGGER.error("Unable to get pdf contents", e);
            throw new RuntimeException("Unable to get pdf contents", e);
        }
    }

    @Override
    public void updateReportScheduler(ReportShareSchedule reportShareSchedule) {
    	Long runCount = reportShareSchedule.getRunCount();
        if (runCount == null) {
            runCount = (long) 0;
        }
        
        reportShareScheduleRepository.update(reportShareSchedule.getId(), runCount + 1);
    }

    @Override
    public byte[] getContentsAsByte(Long reportShareScheduleId) {
        final ReportShareSchedule reportShareSchedule = reportShareScheduleRepository.findOne(reportShareScheduleId);

        try {
            ReportFormat reportFormat = reportShareSchedule.getReportFormat();

            switch (reportFormat) {
                case CSV:
                    return getCsvContent(reportShareSchedule.getReport());

                case PDF:
                    return getPdfContent(reportShareSchedule.getReport());
            }
        } catch (Exception e) {
            LOGGER.error("[event:ONLINE_REPORT] : Unable to send report for scheduler - id: {} reportId: {} ", reportShareSchedule.getId(), reportShareSchedule.getReport().getId(), e);
        }
        return new byte[0];
    }
}
