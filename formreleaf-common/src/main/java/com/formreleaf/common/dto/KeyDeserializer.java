package com.formreleaf.common.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.formreleaf.common.utils.tuple.Tuple2;

import java.io.IOException;

/**
 * @author Bazlur Rahman Rokon
 * @date 7/10/15.
 */
public class KeyDeserializer extends com.fasterxml.jackson.databind.KeyDeserializer {
    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String[] split = key.split(":");
        String _0 = split[0].trim();
        String _1 = split[1].trim();

        return Tuple2.valueOf(_0, _1);
    }
}
