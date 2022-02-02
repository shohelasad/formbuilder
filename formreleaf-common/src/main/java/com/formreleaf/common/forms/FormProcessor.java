package com.formreleaf.common.forms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.formreleaf.common.dto.PeopleDTO;
import com.formreleaf.common.dto.ReportDefinitionDto;
import com.formreleaf.common.utils.StringUtils;
import com.formreleaf.common.utils.tuple.Tuple;
import com.formreleaf.common.utils.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/10/15.
 */
@Component
public class FormProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormProcessor.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    public Optional<Form> process(String jsonObject) {

        try {
            Form form = objectMapper.readValue(jsonObject, Form.class);

            return Optional.of(form);

        } catch (IOException e) {
            LOGGER.error("Unable to process form,", e);
            throw new RuntimeException(e);
        }
    }

    public String processReportDefinition(ReportDefinitionDto reportDefinitionDto) {

        try {
            return objectMapper.writeValueAsString(reportDefinitionDto);

        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to process,", e);
            throw new RuntimeException(e);
        }
    }

    public ReportDefinitionDto processReportDefinition(String json) {
        try {
            return objectMapper.readValue(json, ReportDefinitionDto.class);

        } catch (IOException e) {
            LOGGER.error("Unable to process", e);
            throw new RuntimeException(e);
        }
    }


    public Optional<Form> processProgramForm(String jsonData) {
        try {
            Form[] form = objectMapper.readValue(jsonData, Form[].class);

            return Optional.of(form[0]);

        } catch (IOException e) {
            LOGGER.error("Unable to process form,", e);
            throw new RuntimeException(e);
        }
    }

    public Map<String, Tuple2<String, String>> getSelectedFieldsInMap(Form form) {
        Map<String, Tuple2<String, String>> map = new HashMap<>();

        form.getSections()
                .forEach(section -> section.getBlocks().
                        forEach(block -> block.getFields()
                                .forEach(field -> map.put(field.getName(), Tuple.valueOf(field.getValue(), null/*field.getComment()*/)))));
        return map;
    }

    public String generateJson() {

        Form form = new Form();
        form.setId(1);
        form.setName("Participant");

        Section participantInformationSection = new Section();
        participantInformationSection.setId(1);
        participantInformationSection.setTitle("Participant Information");

        //region 1: Personal Block
        Block participantBlock = new Block(1, "Personal");

        Field firstName = new Field(FieldType.TEXTFIELD, "First Name", "Please enter your first name");
        firstName.setValidator(new Validator(ValidationType.TEXT, true, 2, 50));
        participantBlock.getFields().add(firstName);

        Field middleName = new Field(FieldType.TEXTFIELD, "Middle Name", "Please enter your middle name");
        middleName.setValidator(new Validator(ValidationType.TEXT, false, 2, 50));
        participantBlock.getFields().add(middleName);

        Field lastName = new Field(FieldType.TEXTFIELD, "Last Name", "Please enter your last name");
        lastName.setValidator(new Validator(ValidationType.TEXT, true, 2, 50));
        participantBlock.getFields().add(lastName);

        Field suffix = new Field(FieldType.TEXTFIELD, "Suffix", "Please enter your name Suffix");
        lastName.setValidator(new Validator(ValidationType.TEXT, true, 2, 10));
        participantBlock.getFields().add(suffix);

        Field dateOfBirth = new Field(FieldType.DATE, "Birth Date", "Please enter your date of birth");
        dateOfBirth.setValidator(new Validator(true));
        participantBlock.getFields().add(dateOfBirth);

        Field gender = new Field(FieldType.DROPDOWN, "Gender", "Please enter your Gender");
        gender.setValidator(new Validator(true));
        gender.setOptions(new ArrayList<Option>() {{
            add(new Option(1, "Male", "Male"));
            add(new Option(2, "Female", "Female"));
            add(new Option(3, "Other", "Other"));
        }});
        participantBlock.getFields().add(gender);

        Field schoolName = new Field(FieldType.TEXTFIELD, "School Name", "Please enter your school name");
        schoolName.setValidator(new Validator(true, 5, 200));
        participantBlock.getFields().add(schoolName);

        participantInformationSection.getBlocks().add(participantBlock);
        //endregion

        //region 2: Additional Block
        Block additionalBlock = new Block(2, "Additional");

        Field citizenship = new Field(FieldType.COUNTRY, "Citizenship", "Please enter citizenship country name");
        citizenship.setValidator(new Validator(true));
        additionalBlock.getFields().add(citizenship);

        Field eyeColor = new Field(FieldType.TEXTFIELD, "Eye Color", "Please enter your eye color");
        eyeColor.setValidator(new Validator(false));
        additionalBlock.getFields().add(eyeColor);

        Field identifyingMarks = new Field(FieldType.TEXTFIELD, "Identifying marks", "Please enter Identifying marks");
        identifyingMarks.setValidator(new Validator(false));
        additionalBlock.getFields().add(identifyingMarks);

        Field tShirtSize = new Field(FieldType.DROPDOWN, "T-shirt size", "Please enter T-shirt size");
        tShirtSize.setValidator(new Validator(false));
        tShirtSize.getOptions().add(new Option(1, "Youth Small(4-6)"));
        tShirtSize.getOptions().add(new Option(2, "Youth Medium(8-10)"));
        tShirtSize.getOptions().add(new Option(3, "Youth Large(12-14)"));
        tShirtSize.getOptions().add(new Option(4, "Adult Small"));
        tShirtSize.getOptions().add(new Option(5, "Adult Medium"));
        tShirtSize.getOptions().add(new Option(6, "Adult Large"));
        tShirtSize.getOptions().add(new Option(7, "Adult XL"));
        tShirtSize.getOptions().add(new Option(8, "Adult XXL"));
        tShirtSize.getOptions().add(new Option(9, "Adult XXXL"));
        additionalBlock.getFields().add(tShirtSize);

        Field height = new Field(FieldType.TEXTFIELD, "Height (inches)", "Please enter height in inches");
        height.setValidator(new Validator(ValidationType.NUMBER, true));
        height.getValidator().setMinLength(1);
        height.getValidator().setMaxLength(200);
        additionalBlock.getFields().add(height);

        Field weight = new Field(FieldType.TEXTFIELD, "Weight (pounds)", "Please enter weight in pounds");
        weight.setValidator(new Validator(ValidationType.NUMBER, true));
        weight.getValidator().setMinLength(1);
        weight.getValidator().setMaxLength(500);
        additionalBlock.getFields().add(weight);

        Field studentId = new Field(FieldType.TEXTFIELD, "Membership/Student ID", "Please enter Membership/Student ID");
        studentId.setValidator(new Validator(true));
        studentId.getValidator().setPattern("^[A-Za-z_][A-Za-z\\d_]*$");
        studentId.getValidator().setErrorMessage("Membership/Student ID is not valid");
        additionalBlock.getFields().add(studentId);

        participantInformationSection.getBlocks().add(additionalBlock);

        //endregion

        //region 3: Contact Block

        Block contactBlock = new Block(3, "Contact");

        Field contactEmail = new Field(FieldType.TEXTFIELD, "Email", "Please enter email address");
        contactEmail.setValidator(new Validator(ValidationType.EMAIL, true));
        contactBlock.getFields().add(contactEmail);

        Field contactHomePhone = new Field(FieldType.TEXTFIELD, "Home Phone", "Please enter home Phone");
        contactHomePhone.setValidator(new Validator(ValidationType.PHONE, true));
        contactBlock.getFields().add(contactHomePhone);

        Field contactCellPhone = new Field(FieldType.TEXTFIELD, "Cell Phone", "Please enter cell phone");
        contactCellPhone.setValidator(new Validator(ValidationType.PHONE, true));
        contactBlock.getFields().add(contactCellPhone);

        participantInformationSection.getBlocks().add(contactBlock);
        //endregion

        //region 4: Work Block
        Block workBlock = new Block(4, "Work");

        Field workEmployer = new Field(FieldType.TEXTFIELD, "Employer", "Please enter Employer");
        workEmployer.setValidator(new Validator(false));
        workBlock.getFields().add(workEmployer);

        Field workTitle = new Field(FieldType.TEXTFIELD, "Title", "Please enter Title");
        workTitle.setValidator(new Validator(false));
        workBlock.getFields().add(workTitle);

        Field workPhone = new Field(FieldType.TEXTFIELD, "Work Phone", "Please enter work phone");
        workPhone.setValidator(new Validator(ValidationType.PHONE, false));
        workBlock.getFields().add(workPhone);

        Field occupation = new Field(FieldType.TEXTFIELD, "Title", "Please enter Occupation");
        occupation.setValidator(new Validator(false));
        workBlock.getFields().add(occupation);

        participantInformationSection.getBlocks().add(workBlock);
        //endregion

        //region 5: Eduction Block
        Block educationBlock = new Block(5, "Education");

        Field educationSchoolName = new Field(FieldType.TEXTFIELD, "School Name", "Please enter school name");
        educationSchoolName.setValidator(new Validator(false));
        educationBlock.getFields().add(educationSchoolName);

        Field graduationYear = new Field(FieldType.TEXTFIELD, "Graduation Year", "Please enter Graduation Year");
        graduationYear.setValidator(new Validator(false));
        graduationYear.getValidator().setValidationType(ValidationType.NUMBER);
        //int currentYear = LocalDate.now().getYear();
        //graduationYear.getValidator().setMinLength(currentYear);
        //graduationYear.getValidator().setMaxLength(currentYear + 12);
        educationBlock.getFields().add(graduationYear);

        Field educationGrade = new Field(FieldType.DROPDOWN, "Grade", "Please enter Grade");
        educationGrade.getOptions().addAll(new ArrayList<Option>() {{
            add(new Option(0, ""));
            add(new Option(1, "N/A"));
            add(new Option(2, "Pre-K"));
            add(new Option(3, "1"));
            add(new Option(4, "2"));
            add(new Option(5, "3"));
            add(new Option(6, "4"));
            add(new Option(7, "5"));
            add(new Option(8, "6"));
            add(new Option(9, "7"));
            add(new Option(10, "8"));
            add(new Option(11, "9"));
            add(new Option(12, "10"));
            add(new Option(13, "12"));
            add(new Option(14, "Other"));
        }});
        educationGrade.setValidator(new Validator(false));
        educationBlock.getFields().add(educationGrade);

        participantInformationSection.getBlocks().add(educationBlock);
        //endregion

        //region 6: Others Block
        Block others = new Block(6, "Others");
        participantInformationSection.getBlocks().add(others);
        //endregion

        form.getSections().add(participantInformationSection);

        Section addressSection = new Section(2, "Address");

        //region 1: Home Address Block
        Block homeAddressBlock = new Block(1, "Home Address");

        Field homeAddressAddress = new Field(FieldType.TEXTAREA, "Address", "Please enter address");
        homeAddressAddress.setValidator(new Validator(true));
        homeAddressBlock.getFields().add(homeAddressAddress);

        Field homeAddressAddressLine2 = new Field(FieldType.TEXTAREA, "Address Line 2", "Please enter address line 2");
        homeAddressAddressLine2.setValidator(new Validator(false));
        homeAddressBlock.getFields().add(homeAddressAddressLine2);

        Field homeAddressAddressCity = new Field(FieldType.TEXTFIELD, "City", "Please enter city");
        homeAddressAddressCity.setValidator(new Validator(false));
        homeAddressBlock.getFields().add(homeAddressAddressCity);

        Field homeAddressAddressZipPostalCode = new Field(FieldType.TEXTFIELD, "Zip/Postal Code", "Please enter Zip or Postal Code");
        homeAddressAddressZipPostalCode.setValidator(new Validator(false));
        homeAddressBlock.getFields().add(homeAddressAddressZipPostalCode);

        Field homeAddressAddressCountry = new Field(FieldType.COUNTRY, "Country", "Please enter Country");
        homeAddressAddressCountry.setValidator(new Validator(false));
        homeAddressBlock.getFields().add(homeAddressAddressCountry);

        addressSection.getBlocks().add(homeAddressBlock);
        //endregion

        //region 2: Secondary Address Block
        Block secondaryAddressBlock = new Block(2, "Secondary Address");

        Field secondaryAddressAddress = new Field(FieldType.TEXTAREA, "Address", "Please enter address");
        secondaryAddressAddress.setValidator(new Validator(true));
        secondaryAddressBlock.getFields().add(secondaryAddressAddress);

        Field secondaryAddressAddressLine2 = new Field(FieldType.TEXTAREA, "Address Line 2", "Please enter address line 2");
        secondaryAddressAddressLine2.setValidator(new Validator(false));
        secondaryAddressBlock.getFields().add(secondaryAddressAddressLine2);

        Field secondaryAddressCity = new Field(FieldType.TEXTFIELD, "City", "Please enter city");
        secondaryAddressCity.setValidator(new Validator(false));
        secondaryAddressBlock.getFields().add(secondaryAddressCity);

        Field secondaryAddressZipPostalCode = new Field(FieldType.TEXTFIELD, "Zip/Postal Code", "Please enter Zip or Postal Code");
        secondaryAddressZipPostalCode.setValidator(new Validator(false));
        secondaryAddressBlock.getFields().add(secondaryAddressZipPostalCode);

        Field secondaryAddressCountry = new Field(FieldType.COUNTRY, "Country", "Please enter Country");
        secondaryAddressCountry.setValidator(new Validator(false));
        secondaryAddressBlock.getFields().add(secondaryAddressCountry);

        addressSection.getBlocks().add(secondaryAddressBlock);
        //endregion

        //region 3: Work Address Block
        Block workAddressBlock = new Block(3, "Work Address");

        Field workAddressAddress = new Field(FieldType.TEXTAREA, "Address", "Please enter address");
        workAddressAddress.setValidator(new Validator(true));
        workAddressBlock.getFields().add(workAddressAddress);

        Field workAddressAddressLine2 = new Field(FieldType.TEXTAREA, "Address Line 2", "Please enter address line 2");
        workAddressAddressLine2.setValidator(new Validator(false));
        workAddressBlock.getFields().add(workAddressAddressLine2);

        Field workAddressCity = new Field(FieldType.TEXTFIELD, "City", "Please enter city");
        workAddressCity.setValidator(new Validator(false));
        workAddressBlock.getFields().add(workAddressCity);

        Field workAddressZipPostalCode = new Field(FieldType.TEXTFIELD, "Zip/Postal Code", "Please enter Zip or Postal Code");
        workAddressZipPostalCode.setValidator(new Validator(false));
        workAddressBlock.getFields().add(workAddressZipPostalCode);

        Field workAddressCountry = new Field(FieldType.COUNTRY, "Country", "Please enter Country");
        workAddressCountry.setValidator(new Validator(false));
        workAddressBlock.getFields().add(workAddressCountry);

        addressSection.getBlocks().add(workAddressBlock);
        //endregion

        //region 4: Others Block
        Block addressOthers = new Block(4, "Others");
        addressSection.getBlocks().add(addressOthers);
        //endregion

        form.getSections().add(addressSection);

        Section firstParentSection = new Section(3, "First Parent Or Guardian");

        //region 1: Parent/Guardian Block
        Block parentBlock = new Block(1, "First Parent or Guardian");

        Field parentFirstName = new Field(FieldType.TEXTFIELD, "First Name", "Please enter first name");
        parentFirstName.setValidator(new Validator(true));
        parentBlock.getFields().add(parentFirstName);

        Field parentLastName = new Field(FieldType.TEXTFIELD, "Last Name", "Please enter last name");
        parentLastName.setValidator(new Validator(true));
        parentBlock.getFields().add(parentLastName);

        Field parentRelationship = new Field(FieldType.DROPDOWN, "Relationship", "Please enter relationship");
        parentRelationship.setValidator(new Validator(true));
        parentRelationship.getOptions().addAll(new ArrayList<Option>() {{
            add(new Option(0, ""));
            add(new Option(1, "Mother"));
            add(new Option(2, "Father"));
            add(new Option(3, "Parent"));
            add(new Option(4, "Legal Guardian"));
            add(new Option(5, "Other"));
        }});
        parentBlock.getFields().add(parentRelationship);

        Field custodial = new Field(FieldType.CHECKBOX, "Custodial", "Please enter custodial");
        custodial.setValidator(new Validator(true));
        parentBlock.getFields().add(custodial);

        firstParentSection.getBlocks().add(parentBlock);
        //endregion

        //region 2: Contact Block

        Block parentGuardianContactBlock = new Block(2, "Contact");

        Field parentGuardianContactEmail = new Field(FieldType.TEXTFIELD, "Email", "Please enter email address");
        parentGuardianContactEmail.setValidator(new Validator(ValidationType.EMAIL, true));
        parentGuardianContactBlock.getFields().add(parentGuardianContactEmail);

        Field parentGuardianContactHomePhone = new Field(FieldType.TEXTFIELD, "Home Phone", "Please enter home Phone");
        parentGuardianContactHomePhone.setValidator(new Validator(ValidationType.PHONE, true));
        parentGuardianContactBlock.getFields().add(parentGuardianContactHomePhone);

        Field parentGuardianContactCellPhone = new Field(FieldType.TEXTFIELD, "Cell Phone", "Please enter cell phone");
        parentGuardianContactCellPhone.setValidator(new Validator(ValidationType.PHONE, true));
        parentGuardianContactBlock.getFields().add(parentGuardianContactCellPhone);

        firstParentSection.getBlocks().add(parentGuardianContactBlock);
        //endregion

        //region 3: Work Block
        Block parentGuardianWorkBlock = new Block(3, "Work");

        Field parentGuardianWorkEmployer = new Field(FieldType.TEXTFIELD, "Employer", "Please enter Employer");
        parentGuardianWorkEmployer.setValidator(new Validator(false));
        parentGuardianWorkBlock.getFields().add(parentGuardianWorkEmployer);

        Field parentGuardianWorkTitle = new Field(FieldType.TEXTFIELD, "Title", "Please enter Title");
        parentGuardianWorkTitle.setValidator(new Validator(false));
        parentGuardianWorkBlock.getFields().add(parentGuardianWorkTitle);

        Field parentGuardianWorkPhone = new Field(FieldType.TEXTFIELD, "Work Phone", "Please enter work phone");
        parentGuardianWorkPhone.setValidator(new Validator(ValidationType.PHONE, false));
        parentGuardianWorkBlock.getFields().add(parentGuardianWorkPhone);

        Field parentGuardianOccupation = new Field(FieldType.TEXTFIELD, "Title", "Please enter Occupation");
        parentGuardianOccupation.setValidator(new Validator(false));
        parentGuardianWorkBlock.getFields().add(parentGuardianOccupation);

        firstParentSection.getBlocks().add(parentGuardianWorkBlock);
        //endregion

        //region 4: Primary Address Block
        Block primaryAddressBlock = new Block(4, "Primary Address");

        Field primaryAddressAddress = new Field(FieldType.TEXTAREA, "Address", "Please enter address");
        primaryAddressAddress.setValidator(new Validator(true));
        primaryAddressBlock.getFields().add(primaryAddressAddress);

        Field primaryAddressAddressLine2 = new Field(FieldType.TEXTAREA, "Address Line 2", "Please enter address line 2");
        primaryAddressAddressLine2.setValidator(new Validator(false));
        primaryAddressBlock.getFields().add(primaryAddressAddressLine2);

        Field primaryAddressCity = new Field(FieldType.TEXTFIELD, "City", "Please enter city");
        primaryAddressCity.setValidator(new Validator(false));
        primaryAddressBlock.getFields().add(primaryAddressCity);

        Field primaryAddressZipPostalCode = new Field(FieldType.TEXTFIELD, "Zip/Postal Code", "Please enter Zip or Postal Code");
        primaryAddressZipPostalCode.setValidator(new Validator(false));
        primaryAddressBlock.getFields().add(primaryAddressZipPostalCode);

        Field primaryAddressCountry = new Field(FieldType.COUNTRY, "Country", "Please enter Country");
        primaryAddressCountry.setValidator(new Validator(false));
        primaryAddressBlock.getFields().add(primaryAddressCountry);

        firstParentSection.getBlocks().add(primaryAddressBlock);
        //endregion

        //region 5: Work Address Block
        Block parentWorkAddressBlock = new Block(5, "Work Address");

        Field parentWorkAddressAddress = new Field(FieldType.TEXTAREA, "Address", "Please enter address");
        parentWorkAddressAddress.setValidator(new Validator(true));
        parentWorkAddressBlock.getFields().add(parentWorkAddressAddress);

        Field parentWorkAddressAddressLine2 = new Field(FieldType.TEXTAREA, "Address Line 2", "Please enter address line 2");
        parentWorkAddressAddressLine2.setValidator(new Validator(false));
        parentWorkAddressBlock.getFields().add(parentWorkAddressAddressLine2);

        Field parentWorkAddressCity = new Field(FieldType.TEXTFIELD, "City", "Please enter city");
        parentWorkAddressCity.setValidator(new Validator(false));
        parentWorkAddressBlock.getFields().add(parentWorkAddressCity);

        Field parentWorkAddressZipPostalCode = new Field(FieldType.TEXTFIELD, "Zip/Postal Code", "Please enter Zip or Postal Code");
        parentWorkAddressZipPostalCode.setValidator(new Validator(false));
        parentWorkAddressBlock.getFields().add(parentWorkAddressZipPostalCode);

        Field parentWorkAddressCountry = new Field(FieldType.COUNTRY, "Country", "Please enter Country");
        parentWorkAddressCountry.setValidator(new Validator(false));
        parentWorkAddressBlock.getFields().add(parentWorkAddressCountry);

        firstParentSection.getBlocks().add(parentWorkAddressBlock);
        //endregion

        //region 6: Others Block
        Block parentOthersBlock = new Block(6, "Others");
        firstParentSection.getBlocks().add(parentOthersBlock);
        //endregion

        form.getSections().add(firstParentSection);

        Section secondParentSection = new Section(4, "Second Parent Or Guardian");

        //region 1: Parent/Guardian Block
        Block secondParentBlock = new Block(1, "Second Parent or Guardian");

        Field secondParentFirstName = new Field(FieldType.TEXTFIELD, "First Name", "Please enter first name");
        secondParentFirstName.setValidator(new Validator(true));
        secondParentBlock.getFields().add(secondParentFirstName);

        Field secondParentLastName = new Field(FieldType.TEXTFIELD, "Last Name", "Please enter last name");
        secondParentLastName.setValidator(new Validator(true));
        secondParentBlock.getFields().add(secondParentLastName);

        Field secondParentRelationship = new Field(FieldType.DROPDOWN, "Relationship", "Please enter relationship");
        secondParentRelationship.setValidator(new Validator(true));
        secondParentRelationship.getOptions().addAll(new ArrayList<Option>() {{
            add(new Option(0, ""));
            add(new Option(1, "Mother"));
            add(new Option(2, "Father"));
            add(new Option(3, "Parent"));
            add(new Option(4, "Legal Guardian"));
            add(new Option(5, "Other"));
        }});
        secondParentBlock.getFields().add(secondParentRelationship);

        Field secondCustodial = new Field(FieldType.CHECKBOX, "Custodial", "Please enter custodial");
        secondCustodial.setValidator(new Validator(true));
        secondParentBlock.getFields().add(secondCustodial);

        secondParentSection.getBlocks().add(secondParentBlock);
        //endregion

        //region 2: Contact Block

        Block secondParentGuardianContactBlock = new Block(2, "Contact");

        Field secondParentGuardianContactEmail = new Field(FieldType.TEXTFIELD, "Email", "Please enter email address");
        secondParentGuardianContactEmail.setValidator(new Validator(ValidationType.EMAIL, true));
        secondParentGuardianContactBlock.getFields().add(secondParentGuardianContactEmail);

        Field secondParentGuardianContactHomePhone = new Field(FieldType.TEXTFIELD, "Home Phone", "Please enter home Phone");
        secondParentGuardianContactHomePhone.setValidator(new Validator(ValidationType.PHONE, true));
        secondParentGuardianContactBlock.getFields().add(secondParentGuardianContactHomePhone);

        Field secondParentGuardianContactCellPhone = new Field(FieldType.TEXTFIELD, "Cell Phone", "Please enter cell phone");
        secondParentGuardianContactCellPhone.setValidator(new Validator(ValidationType.PHONE, true));
        secondParentGuardianContactBlock.getFields().add(secondParentGuardianContactCellPhone);

        secondParentSection.getBlocks().add(secondParentGuardianContactBlock);
        //endregion

        //region 3: Work Block
        Block secondParentGuardianWorkBlock = new Block(3, "Work");

        Field secondParentGuardianWorkEmployer = new Field(FieldType.TEXTFIELD, "Employer", "Please enter Employer");
        secondParentGuardianWorkEmployer.setValidator(new Validator(false));
        secondParentGuardianWorkBlock.getFields().add(secondParentGuardianWorkEmployer);

        Field secondParentGuardianWorkTitle = new Field(FieldType.TEXTFIELD, "Title", "Please enter Title");
        secondParentGuardianWorkTitle.setValidator(new Validator(false));
        secondParentGuardianWorkBlock.getFields().add(secondParentGuardianWorkTitle);

        Field secondParentGuardianWorkPhone = new Field(FieldType.TEXTFIELD, "Work Phone", "Please enter work phone");
        secondParentGuardianWorkPhone.setValidator(new Validator(ValidationType.PHONE, false));
        secondParentGuardianWorkBlock.getFields().add(secondParentGuardianWorkPhone);

        Field secondParentGuardianOccupation = new Field(FieldType.TEXTFIELD, "Title", "Please enter Occupation");
        secondParentGuardianOccupation.setValidator(new Validator(false));
        secondParentGuardianWorkBlock.getFields().add(secondParentGuardianOccupation);

        secondParentSection.getBlocks().add(secondParentGuardianWorkBlock);
        //endregion

        //region 4: Primary Address Block
        Block secondParentPrimaryAddressBlock = new Block(4, "Primary Address");

        Field secondParentPrimaryAddressAddress = new Field(FieldType.TEXTAREA, "Address", "Please enter address");
        secondParentPrimaryAddressAddress.setValidator(new Validator(true));
        secondParentPrimaryAddressBlock.getFields().add(secondParentPrimaryAddressAddress);

        Field secondParentPrimaryAddressAddressLine2 = new Field(FieldType.TEXTAREA, "Address Line 2", "Please enter address line 2");
        secondParentPrimaryAddressAddressLine2.setValidator(new Validator(false));
        secondParentPrimaryAddressBlock.getFields().add(secondParentPrimaryAddressAddressLine2);

        Field secondParentPrimaryAddressCity = new Field(FieldType.TEXTFIELD, "City", "Please enter city");
        secondParentPrimaryAddressCity.setValidator(new Validator(false));
        secondParentPrimaryAddressBlock.getFields().add(secondParentPrimaryAddressCity);

        Field secondParentPrimaryAddressZipPostalCode = new Field(FieldType.TEXTFIELD, "Zip/Postal Code", "Please enter Zip or Postal Code");
        secondParentPrimaryAddressZipPostalCode.setValidator(new Validator(false));
        secondParentPrimaryAddressBlock.getFields().add(secondParentPrimaryAddressZipPostalCode);

        Field secondParentPrimaryAddressCountry = new Field(FieldType.COUNTRY, "Country", "Please enter Country");
        secondParentPrimaryAddressCountry.setValidator(new Validator(false));
        secondParentPrimaryAddressBlock.getFields().add(secondParentPrimaryAddressCountry);

        secondParentSection.getBlocks().add(secondParentPrimaryAddressBlock);
        //endregion

        //region 5: Work Address Block
        Block secondParentParentWorkAddressBlock = new Block(5, "Work Address");

        Field secondParentParentWorkAddressAddress = new Field(FieldType.TEXTAREA, "Address", "Please enter address");
        secondParentParentWorkAddressAddress.setValidator(new Validator(true));
        secondParentParentWorkAddressBlock.getFields().add(secondParentParentWorkAddressAddress);

        Field secondParentParentWorkAddressAddressLine2 = new Field(FieldType.TEXTAREA, "Address Line 2", "Please enter address line 2");
        secondParentParentWorkAddressAddressLine2.setValidator(new Validator(false));
        secondParentParentWorkAddressBlock.getFields().add(secondParentParentWorkAddressAddressLine2);

        Field secondParentParentWorkAddressCity = new Field(FieldType.TEXTFIELD, "City", "Please enter city");
        secondParentParentWorkAddressCity.setValidator(new Validator(false));
        secondParentParentWorkAddressBlock.getFields().add(secondParentParentWorkAddressCity);

        Field secondParentParentWorkAddressZipPostalCode = new Field(FieldType.TEXTFIELD, "Zip/Postal Code", "Please enter Zip or Postal Code");
        secondParentParentWorkAddressZipPostalCode.setValidator(new Validator(false));
        secondParentParentWorkAddressBlock.getFields().add(secondParentParentWorkAddressZipPostalCode);

        Field secondParentParentWorkAddressCountry = new Field(FieldType.COUNTRY, "Country", "Please enter Country");
        secondParentParentWorkAddressCountry.setValidator(new Validator(false));
        secondParentParentWorkAddressBlock.getFields().add(secondParentParentWorkAddressCountry);

        secondParentSection.getBlocks().add(secondParentParentWorkAddressBlock);
        //endregion

        //region 6: Others Block
        Block secondParentParentOthersBlock = new Block(6, "Others");
        secondParentSection.getBlocks().add(secondParentParentOthersBlock);
        //endregion

        form.getSections().add(secondParentSection);

        Section emergencyContactSection = new Section(5, "Emergency Contacts");

        //region 1: First Emergency Contact Block
        Block firstEmergencyContactBlock = new Block(1, "First Emergency Contact");

        Field firstEmergencyContactFirstName = new Field(FieldType.TEXTFIELD, "First Name", "Please enter first name");
        firstEmergencyContactFirstName.setValidator(new Validator(ValidationType.TEXT, true, 2, 50));
        firstEmergencyContactBlock.getFields().add(firstEmergencyContactFirstName);

        Field firstEmergencyContactLastName = new Field(FieldType.TEXTFIELD, "Last Name", "Please enter last name");
        firstEmergencyContactLastName.setValidator(new Validator(ValidationType.TEXT, true, 2, 50));
        firstEmergencyContactBlock.getFields().add(firstEmergencyContactLastName);

        Field firstEmergencyContactHomePhone = new Field(FieldType.TEXTFIELD, "Home Phone", "Please enter home phone");
        firstEmergencyContactHomePhone.setValidator(new Validator(ValidationType.PHONE, true));
        firstEmergencyContactBlock.getFields().add(firstEmergencyContactHomePhone);

        Field firstEmergencyContactCellPhone = new Field(FieldType.TEXTFIELD, "Cell Phone", "Please enter cell phone");
        firstEmergencyContactCellPhone.setValidator(new Validator(ValidationType.PHONE, true));
        firstEmergencyContactBlock.getFields().add(firstEmergencyContactCellPhone);

        Field firstEmergencyContactEmail = new Field(FieldType.TEXTFIELD, "Email", "Please enter Email");
        firstEmergencyContactEmail.setValidator(new Validator(ValidationType.EMAIL, true));
        firstEmergencyContactBlock.getFields().add(firstEmergencyContactEmail);

        Field firstEmergencyContactRelationship = new Field(FieldType.DROPDOWN, "Relationship", "Please enter relationship");
        firstEmergencyContactRelationship.setValidator(new Validator(true));
        firstEmergencyContactRelationship.getOptions().addAll(new ArrayList<Option>() {{
            add(new Option(0, ""));
            add(new Option(1, "Mother"));
            add(new Option(2, "Father"));
            add(new Option(3, "Parent"));
            add(new Option(4, "Legal Guardian"));
            add(new Option(5, "Other"));
        }});
        firstEmergencyContactBlock.getFields().add(firstEmergencyContactRelationship);

        Field firstEmergencyContactMayPickup = new Field(FieldType.CHECKBOX, "May pick up", "");
        firstEmergencyContactMayPickup.setValidator(new Validator(true));
        firstEmergencyContactBlock.getFields().add(firstEmergencyContactMayPickup);

        emergencyContactSection.getBlocks().add(firstEmergencyContactBlock);
        //endregion

        //region 2: Second Emergency Contact Block
        Block SecondEmergencyContactBlock = new Block(2, "Second Emergency Contact");

        Field secondEmergencyContactFirstName = new Field(FieldType.TEXTFIELD, "First Name", "Please enter first name");
        secondEmergencyContactFirstName.setValidator(new Validator(ValidationType.TEXT, true, 2, 50));
        SecondEmergencyContactBlock.getFields().add(secondEmergencyContactFirstName);

        Field secondEmergencyContactLastName = new Field(FieldType.TEXTFIELD, "Last Name", "Please enter last name");
        secondEmergencyContactLastName.setValidator(new Validator(ValidationType.TEXT, true, 2, 50));
        SecondEmergencyContactBlock.getFields().add(secondEmergencyContactLastName);

        Field secondEmergencyContactHomePhone = new Field(FieldType.TEXTFIELD, "Home Phone", "Please enter home phone");
        secondEmergencyContactHomePhone.setValidator(new Validator(ValidationType.PHONE, true));
        SecondEmergencyContactBlock.getFields().add(secondEmergencyContactHomePhone);

        Field secondEmergencyContactCellPhone = new Field(FieldType.TEXTFIELD, "Cell Phone", "Please enter cell phone");
        secondEmergencyContactCellPhone.setValidator(new Validator(ValidationType.PHONE, true));
        SecondEmergencyContactBlock.getFields().add(secondEmergencyContactCellPhone);

        Field secondEmergencyContactEmail = new Field(FieldType.TEXTFIELD, "Email", "Please enter Email");
        secondEmergencyContactEmail.setValidator(new Validator(ValidationType.EMAIL, true));
        SecondEmergencyContactBlock.getFields().add(secondEmergencyContactEmail);

        Field secondEmergencyContactRelationship = new Field(FieldType.DROPDOWN, "Relationship", "Please enter relationship");
        secondEmergencyContactRelationship.setValidator(new Validator(true));
        secondEmergencyContactRelationship.getOptions().addAll(new ArrayList<Option>() {{
            add(new Option(0, ""));
            add(new Option(1, "Mother"));
            add(new Option(2, "Father"));
            add(new Option(3, "Parent"));
            add(new Option(4, "Legal Guardian"));
            add(new Option(5, "Other"));
        }});
        SecondEmergencyContactBlock.getFields().add(secondEmergencyContactRelationship);

        Field secondEmergencyContactMayPickup = new Field(FieldType.CHECKBOX, "May pick up", "");
        secondEmergencyContactMayPickup.setValidator(new Validator(true));
        SecondEmergencyContactBlock.getFields().add(secondEmergencyContactMayPickup);

        emergencyContactSection.getBlocks().add(SecondEmergencyContactBlock);
        //endregion

        form.getSections().add(emergencyContactSection);

        Section physicianSection = new Section(6, "Physicians");

        //region 1: Primary Care Physician Block
        Block physicianPrimaryBlock = new Block(1, "Primary Care Physician");

        Field physicianPrimaryFirstName = new Field(FieldType.TEXTFIELD, "First Name", "Please enter first name");
        physicianPrimaryFirstName.setValidator(new Validator(true));
        physicianPrimaryBlock.getFields().add(physicianPrimaryFirstName);

        Field physicianPrimaryLastName = new Field(FieldType.TEXTFIELD, "Last Name", "Please enter last name");
        physicianPrimaryLastName.setValidator(new Validator(true));
        physicianPrimaryBlock.getFields().add(physicianPrimaryLastName);

        Field physicianPrimaryWorkPhone = new Field(FieldType.TEXTFIELD, "Work Phone", "Please enter work phone");
        physicianPrimaryWorkPhone.setValidator(new Validator(ValidationType.PHONE, true));
        physicianPrimaryBlock.getFields().add(physicianPrimaryWorkPhone);

        physicianSection.getBlocks().add(physicianPrimaryBlock);

        //endregion

        //region 2: Physician Other Block
        Block physicianOtherBlock = new Block(2, "Other");

        Field physicianOtherFirstName = new Field(FieldType.TEXTFIELD, "First Name", "Please enter first name");
        physicianOtherFirstName.setValidator(new Validator(true));
        physicianOtherBlock.getFields().add(physicianOtherFirstName);

        Field physicianOtherLastName = new Field(FieldType.TEXTFIELD, "Last Name", "Please enter last name");
        physicianOtherLastName.setValidator(new Validator(true));
        physicianOtherBlock.getFields().add(physicianOtherLastName);

        Field physicianOtherWorkPhone = new Field(FieldType.TEXTFIELD, "Work Phone", "Please enter work phone");
        physicianOtherWorkPhone.setValidator(new Validator(ValidationType.PHONE, true));
        physicianOtherBlock.getFields().add(physicianOtherWorkPhone);

        Field physicianOtherPhysicianType = new Field(FieldType.TEXTFIELD, "Physician Type ", "Please enter Physician Type");
        physicianOtherPhysicianType.setValidator(new Validator(ValidationType.TEXT, true));
        physicianOtherBlock.getFields().add(physicianOtherPhysicianType);

        physicianSection.getBlocks().add(physicianOtherBlock);
        //endregion

        form.getSections().add(physicianSection);
        Section insuranceSection = new Section(7, "Insurance");

        //region 1: Insurance Block
        Block insuranceBlock = new Block(1, "Insurance");

        Field career = new Field(FieldType.TEXTFIELD, "Carrier", "Please enter carrier name");
        career.setValidator(new Validator(true));
        insuranceBlock.getFields().add(career);

        Field subscriberName = new Field(FieldType.TEXTFIELD, "Subscriber name ", "Please enter subscriber name");
        subscriberName.setValidator(new Validator(true));
        insuranceBlock.getFields().add(subscriberName);

        Field groupNumber = new Field(FieldType.TEXTFIELD, "Group number  ", "Please enter group number ");
        groupNumber.setValidator(new Validator(true));
        insuranceBlock.getFields().add(groupNumber);

        insuranceSection.getBlocks().add(insuranceBlock);
        //endregion

        //region 2: Others Block
        Block insuranceOtherBlock = new Block(2, "Others");
        insuranceSection.getBlocks().add(insuranceOtherBlock);
        //endregion

        form.getSections().add(insuranceSection);

        Section medicationSection = new Section(8, "Medications");

        //region 1: Medication Block
        MultipleAllowedBlock medicationBlock = new MultipleAllowedBlock(1, "Medication");
        medicationBlock.setAllowMultiple(true);

        Field medicationName = new Field(FieldType.TEXTFIELD, "Name", "Please enter medication name");
        medicationName.setValidator(new Validator(true));
        medicationBlock.getFields().add(medicationName);

        Field diagnosis = new Field(FieldType.TEXTFIELD, "Diagnosis", "Please enter diagnosis");
        diagnosis.setValidator(new Validator(true));
        medicationBlock.getFields().add(diagnosis);

        Field physician = new Field(FieldType.TEXTFIELD, "Physician", "Please enter physician name");
        physician.setValidator(new Validator(true));
        medicationBlock.getFields().add(physician);

        Field dosageDndTimeInstructions = new Field(FieldType.TEXTAREA, "Dosage and time instructions", "Please enter dosage and time instructions ");
        dosageDndTimeInstructions.setValidator(new Validator(true));
        medicationBlock.getFields().add(dosageDndTimeInstructions);

        medicationSection.getBlocks().add(medicationBlock);
        form.getSections().add(medicationSection);
        //endregion

        //Quick and Dirty Sol: keep this id always 9. We will figure out a better solution later,
        //For now  we will teat section id=9 as a `Concerns and Comments` section
        Section concernAndCommentSection = new Section(9, "Concerns and Comments");

        //region 1: Academic/Behavioral Block
        Block academicOrBehavioral = new Block(1, "Academic/Behavioral");

        ConcernField academicField = new ConcernField(FieldType.TEXTAREA, "Academic");
        academicField.setValidator(new Validator(true));
        academicOrBehavioral.getFields().add(academicField);

        ConcernField behavioralField = new ConcernField(FieldType.TEXTAREA, "Behavioral");
        behavioralField.setValidator(new Validator(true));
        academicOrBehavioral.getFields().add(behavioralField);

        ConcernField learningStyleField = new ConcernField(FieldType.TEXTAREA, "Learning style");
        learningStyleField.setValidator(new Validator(true));
        academicOrBehavioral.getFields().add(learningStyleField);

        ConcernField socialField = new ConcernField(FieldType.TEXTAREA, "Social");
        socialField.setValidator(new Validator(true));
        academicOrBehavioral.getFields().add(socialField);

        ConcernField specialNeedsField = new ConcernField(FieldType.TEXTAREA, "Special needs");
        specialNeedsField.setValidator(new Validator(true));
        academicOrBehavioral.getFields().add(specialNeedsField);

        ConcernField strengthAndWeakness = new ConcernField(FieldType.TEXTAREA, "Strengths and weaknesses");
        strengthAndWeakness.setValidator(new Validator(true));
        academicOrBehavioral.getFields().add(strengthAndWeakness);

        concernAndCommentSection.getBlocks().add(academicOrBehavioral);
        //endregion

        //region 2 : Health/Medical
        Block healthMedicalBlock = new Block(2, "Health/Medical");

        healthMedicalBlock.getFields().add(new ConcernField(FieldType.TEXTAREA, "Allergies", true));
        healthMedicalBlock.getFields().add(new ConcernField(FieldType.TEXTAREA, "Asthma", true));
        healthMedicalBlock.getFields().add(new ConcernField(FieldType.TEXTAREA, "Dietary", true));
        healthMedicalBlock.getFields().add(new ConcernField(FieldType.TEXTAREA, "Epi pen", true));
        healthMedicalBlock.getFields().add(new ConcernField(FieldType.TEXTAREA, "Hospitalization", true));
        healthMedicalBlock.getFields().add(new ConcernField(FieldType.TEXTAREA, "Injuries", true));
        healthMedicalBlock.getFields().add(new ConcernField(FieldType.TEXTAREA, "Medical conditions", true));
        healthMedicalBlock.getFields().add(new ConcernField(FieldType.TEXTAREA, "Orthopedic", true));
        healthMedicalBlock.getFields().add(new ConcernField(FieldType.TEXTAREA, "Physical limitations", true));
        healthMedicalBlock.getFields().add(new ConcernField(FieldType.TEXTAREA, "Protective or corrective equipment", true));
        healthMedicalBlock.getFields().add(new ConcernField(FieldType.TEXTAREA, "Seizure Disorder", true));
        healthMedicalBlock.getFields().add(new ConcernField(FieldType.TEXTAREA, "Serious illnesses", true));
        healthMedicalBlock.getFields().add(new ConcernField(FieldType.TEXTAREA, "Vision", true));

        concernAndCommentSection.getBlocks().add(healthMedicalBlock);
        //endregion

        //region 3: Head Injury/Concussion
        Block headInjuryConcussionBlock = new Block(3, "Head Injury/Concussion");
        headInjuryConcussionBlock.getFields().add(new ConcernField(FieldType.TEXTAREA, "Traumatic Head Injury", true));
        headInjuryConcussionBlock.getFields().add(new ConcernField(FieldType.TEXTAREA, "Medical attention for head injury", true));
        headInjuryConcussionBlock.getFields().add(new ConcernField(FieldType.TEXTAREA, "Diagnosed concussion", true));

        concernAndCommentSection.getBlocks().add(headInjuryConcussionBlock);
        //endregion

        form.getSections().add(concernAndCommentSection);

        form.getSections().forEach(section ->
                        section.getBlocks().forEach(block ->
                                block.getFields().forEach(field -> {
                                            field.setName(toCamelCase(normalize(section.getTitle())) + "_"
                                                    + toCamelCase(normalize(block.getTitle())) + "_"
                                                    + toCamelCase(normalize(field.getTitle())));
                                            field.setId(UUID.randomUUID().toString());
                                        }
                                ))
        );

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Form[] forms = {form};

            String json = objectMapper.writeValueAsString(form);
            //System.out.println(json);

            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String normalize(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("/", " ")
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("[^ \\w]", "").trim()
                .replaceAll("\\s+", "_").toLowerCase(Locale.ENGLISH);
    }

    static String toCamelCase(String s) {
        String[] parts = s.split("_");
        String camelCaseString = "";
        for (String part : parts) {
            camelCaseString = camelCaseString + toProperCase(part);
        }

        return camelCaseString.substring(0, 1).toLowerCase() + camelCaseString.substring(1);
    }

    static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }

    public String process(Form form) {
        try {

            return objectMapper.writeValueAsString(form);

        } catch (IOException e) {
            LOGGER.error("Unable to process form,", e);
            throw new RuntimeException(e);
        }
    }


//    public static void main(String[] args) {
//        FormProcessor formProcessor = new FormProcessor();
//        String json = formProcessor.generateJson();
//        formProcessor.getRegistrantInfo(json);
//    }

    //TODO have to revisit, this is quick and dirty work for the sake of demo
    public PeopleDTO getRegistrantInfo(String json) {
        try {
            Form form = new ObjectMapper().readValue(json, Form.class);

            List<Block> blocks = form.getSections().stream()
                    .filter(section -> section.getTitle().equalsIgnoreCase("Participant Information"))
                    .map(section1 -> section1.getBlocks()
                            .stream()
                            .filter(block1 -> block1.getTitle().equalsIgnoreCase("Personal") || block1.getTitle().equalsIgnoreCase("Contact")))
                    .collect(Collectors.toList())
                    .stream()
                    .flatMap(blockStream -> blockStream
                            .map(block2 -> block2))
                    .collect(Collectors.toList());

            Optional<Block> personal = blocks.stream().filter(block1 -> block1.getTitle().equalsIgnoreCase("Personal")).findFirst();
            Optional<Block> contact = blocks.stream().filter(block1 -> block1.getTitle().equalsIgnoreCase("Contact")).findFirst();

            Optional<Field> fistName = personal.isPresent() ? personal.get().getFields().stream().filter(field -> field.getName().equalsIgnoreCase("participantInformation_personal_firstName")).findFirst() : Optional.empty();
            Optional<Field> middleName = personal.isPresent() ? personal.get().getFields().stream().filter(field -> field.getName().equalsIgnoreCase("participantInformation_personal_middleName")).findFirst() : Optional.empty();
            Optional<Field> lastName = personal.isPresent() ? personal.get().getFields().stream().filter(field -> field.getName().equalsIgnoreCase("participantInformation_personal_lastName")).findFirst() : Optional.empty();
            Optional<Field> gender = personal.isPresent() ? personal.get().getFields().stream().filter(field -> field.getName().equalsIgnoreCase("participantInformation_personal_gender")).findFirst() : Optional.empty();
            Optional<Field> dateOfBirth = personal.isPresent() ? personal.get().getFields().stream().filter(field -> field.getName().equalsIgnoreCase("participantInformation_personal_birthDate")).findFirst() : Optional.empty();

            Optional<Field> email = contact.isPresent() ? contact.get().getFields().stream().filter(field -> field.getName().equalsIgnoreCase("participantInformation_contact_email")).findFirst() : Optional.empty();
            Optional<Field> homePhone = contact.isPresent() ? contact.get().getFields().stream().filter(field -> field.getName().equalsIgnoreCase("participantInformation_contact_homePhone")).findFirst() : Optional.empty();
            Optional<Field> cellPhone = contact.isPresent() ? contact.get().getFields().stream().filter(field -> field.getName().equalsIgnoreCase("participantInformation_contact_cellPhone")).findFirst() : Optional.empty();

            PeopleDTO peopleDTO = new PeopleDTO();

            String personFullName = "";
            if (fistName.isPresent()) {
                String value = fistName.get().getValue();
                if (StringUtils.isNotEmpty(value)) {
                    personFullName += value + " ";
                    peopleDTO.setFirstName(value);
                }
            }

            if (middleName.isPresent()) {
                String value = middleName.get().getValue();
                if (StringUtils.isNotEmpty(value)) {
                    personFullName += value + " ";
                    peopleDTO.setMiddleName(value);
                }
            }

            if (lastName.isPresent()) {
                String  value = lastName.get().getValue();
                if (StringUtils.isNotEmpty(value)) {
                    personFullName += value + " ";
                    peopleDTO.setLastName(value);
                }
            }

            peopleDTO.setPerson(personFullName);
            peopleDTO.setGender(gender.isPresent() && StringUtils.isNotEmpty(gender.get().getValue()) ? gender.get().getValue() : "");
            peopleDTO.setDateOfBirth(dateOfBirth.isPresent() && StringUtils.isNotEmpty(dateOfBirth.get().getValue()) ? dateOfBirth.get().getValue() : "");
            peopleDTO.setEmail(email.isPresent() && StringUtils.isNotEmpty(email.get().getValue()) ? email.get().getValue() : "");
            peopleDTO.setHomePhone(homePhone.isPresent() && StringUtils.isNotEmpty(homePhone.get().getValue()) ? homePhone.get().getValue() : "");
            peopleDTO.setCellPhone(cellPhone.isPresent() && StringUtils.isNotEmpty(cellPhone.get().getValue()) ? cellPhone.get().getValue() : "");

            return peopleDTO;
        } catch (IOException e) {
            LOGGER.error("Unable to process form,", e);
            throw new RuntimeException(e);
        }
    }
}
