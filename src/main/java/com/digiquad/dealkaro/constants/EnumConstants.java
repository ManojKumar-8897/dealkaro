package com.digiquad.dealkaro.constants;

import org.springframework.util.StringUtils;

public class EnumConstants {

    public enum Role {
        SUPER_ADMIN, ADMIN, USER;

        @Override
        public String toString() {
            return StringUtils.capitalize(name());
        }
    }
}