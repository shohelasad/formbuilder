package com.formreleaf.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.time.Duration;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/10/15.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApplicationAbout {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonPropertyOrder(alphabetic = true)
    public static class VMProperties {

        private final String version = System.getProperty("java.runtime.version");

        @JsonSerialize(using = ToStringSerializer.class)
        private Duration uptime;

        private Long usedMemory;

        private Long freeMemory;

        private Long totalMemory;

        private Long maxMemory;

        public String getVersion() {
            return version;
        }

        public Duration getUptime() {
            return uptime;
        }

        public void setUptime(Duration uptime) {
            this.uptime = uptime;
        }

        public Long getUsedMemory() {
            return usedMemory;
        }

        public void setUsedMemory(Long usedMemory) {
            this.usedMemory = usedMemory;
        }

        public Long getFreeMemory() {
            return freeMemory;
        }

        public void setFreeMemory(Long freeMemory) {
            this.freeMemory = freeMemory;
        }

        public Long getTotalMemory() {
            return totalMemory;
        }

        public void setTotalMemory(Long totalMemory) {
            this.totalMemory = totalMemory;
        }

        public Long getMaxMemory() {
            return maxMemory;
        }

        public void setMaxMemory(Long maxMemory) {
            this.maxMemory = maxMemory;
        }
    }

    private VMProperties vm;

    private String serverName;

    public VMProperties getVm() {
        return vm;
    }

    public void setVm(VMProperties vm) {
        this.vm = vm;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
