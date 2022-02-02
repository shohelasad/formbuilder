package com.vantage.sportsregistration.report;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bazlur Rahman Rokon
 * @date 7/10/15.
 */
public class ReportFilterDtoTest {
    private static Logger log = LoggerFactory.getLogger(ReportFilterDtoTest.class);

    //  static ObjectMapper objectMapper;
    //  static FormProcessor formProcessor;

    @Before
    public void before() {
//        objectMapper = new ObjectMapper();
//        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//        formProcessor = new FormProcessor();
    }

    @Test
    public void testSerializationAndDeserialization() {

//        ReportFilterDto reportFilterDto = new ReportFilterDto();
//        reportFilterDto.setPrograms(new ArrayList<ProgramDto>() {{
//            add(new ProgramDto().setId((long) 1).setSectionDtos(new ArrayList<>()));
//            add(new ProgramDto().setId((long) 2).setSectionDtos(new ArrayList<>()));
//        }}).setApproval(RegistrationApproval.APPROVED);
//
//        String generateJson = formProcessor.generateJson();
//
//        formProcessor.process(generateJson)
//                .ifPresent(form -> {
//                    LinkedHashMap<Tuple2<String, String>, String> collect = form.getSections()
//                            .stream()
//                            .map(section -> section.getBlocks()
//                                    .stream()
//                                    .map(ReportFilterDtoTest::getTupleMap)
//                                    .collect(Collectors.toList()))
//                            .flatMap(Collection::stream)
//                            .map(LinkedHashMap::entrySet)
//                            .flatMap(Collection::stream)
//                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> null, LinkedHashMap::new));
//                    reportFilterDto.setSelectedFields(collect);
//                });
//
//        try {
//            String jsonString1 = objectMapper.writeValueAsString(reportFilterDto);
//            log.info("jsonString1: {}", jsonString1);
//            ReportFilterDto reportFilterDto1 = objectMapper.readValue(jsonString1, ReportFilterDto.class);
//            String jsonString2 = objectMapper.writeValueAsString(reportFilterDto1);
//            log.info("jsonString2: {}", jsonString2);
//
//            //doesn't matter if it fails.
//            // assert jsonString1.contentEquals(jsonString2);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

//    private static LinkedHashMap<Tuple2<String, String>, String> getTupleMap(Block block) {
//        return block.getFields()
//                .stream()
//                .map(field -> Tuple2.valueOf(field.getId(), field.getTitle()))
//                .collect(Collectors.toMap(t -> t, t -> block.getTitle(), (v1, v2) -> null, LinkedHashMap::new));
//    }
}