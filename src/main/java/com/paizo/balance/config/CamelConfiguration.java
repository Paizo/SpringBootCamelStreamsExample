package com.paizo.balance.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Class containing Camel's configuration properties
 */
@Configuration
@ConfigurationProperties(prefix="paizo.balance.camel")
@Data
public class CamelConfiguration {

    private Batch batch;
    private File file;

    public static class Batch {
        private long batchTimeout;
        private int maxRecords;

        public long getBatchTimeout() {
            return batchTimeout;
        }

        public void setBatchTimeout(long batchTimeout) {
            this.batchTimeout = batchTimeout;
        }

        public int getMaxRecords() {
            return maxRecords;
        }

        public void setMaxRecords(int maxRecords) {
            this.maxRecords = maxRecords;
        }
    }

    public static class File {
        private Csv csv;
        private Prn prn;

        public Prn getPrn() {
            return prn;
        }

        public void setPrn(Prn prn) {
            this.prn = prn;
        }

        public Csv getCsv() {
            return csv;
        }

        public void setCsv(Csv csv) {
            this.csv = csv;
        }

        public static class Csv {
            private boolean enable;
            private String dir;
            private boolean noop;
            private boolean recursive;
            private String type;
            private long delay;

            public boolean isEnable() {
                return enable;
            }

            public void setEnable(boolean enable) {
                this.enable = enable;
            }

            public long getDelay() {
                return delay;
            }

            public void setDelay(long delay) {
                this.delay = delay;
            }

            public String getDir() {
                return dir;
            }

            public void setDir(String dir) {
                this.dir = dir;
            }

            public boolean isNoop() {
                return noop;
            }

            public void setNoop(boolean noop) {
                this.noop = noop;
            }

            public boolean isRecursive() {
                return recursive;
            }

            public void setRecursive(boolean recursive) {
                this.recursive = recursive;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }

        public static class Prn {
            private boolean enable;
            private String dir;
            private boolean noop;
            private boolean recursive;
            private String type;
            private long delay;

            public boolean isEnable() {
                return enable;
            }

            public void setEnable(boolean enable) {
                this.enable = enable;
            }

            public long getDelay() {
                return delay;
            }

            public void setDelay(long delay) {
                this.delay = delay;
            }

            public String getDir() {
                return dir;
            }

            public void setDir(String dir) {
                this.dir = dir;
            }

            public boolean isNoop() {
                return noop;
            }

            public void setNoop(boolean noop) {
                this.noop = noop;
            }

            public boolean isRecursive() {
                return recursive;
            }

            public void setRecursive(boolean recursive) {
                this.recursive = recursive;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }
}
