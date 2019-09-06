/*
 * Copyright 2019 HP Development Company, L.P.
 * SPDX-License-Identifier: MIT
 */

package com.hp.letscolor.resource;

/**
 * Constants usable by Skill Connections in general
 */
public class SkillConnections {
    public static final String PROVIDER_ID = "providerId";
    public static final String TYPE = "@type";
    public static final String VERSION = "@version";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String URL = "url";
    public static final String IMAGE_TYPE = "imageType";
    public static final String CONTEXT = "context";
    public static final String PRINT_IMAGE_REQUEST = "PrintImageRequest";
    public static final String PRINT_PDF_REQUEST = "PrintPDFRequest";
    public static final String PRINT_WEB_PAGE_REQUEST = "PrintWebPageRequest";
    public static final String CONNECTION_AMAZON_PRINT_PDF_1 = "connection://AMAZON.PrintPDF/1";
    public static final String CONNECTION_AMAZON_PRINT_IMAGE_1 = "connection://AMAZON.PrintImage/1";
    public static final String CONNECTION_AMAZON_PRINT_WEB_PAGE_1 = "connection://AMAZON.PrintWebPage/1";
    public static final ConnectionTask PRINT_PDF = new ConnectionTask(CONNECTION_AMAZON_PRINT_IMAGE_1, PRINT_IMAGE_REQUEST);
    public static final ConnectionTask PRINT_IMAGE = new ConnectionTask(CONNECTION_AMAZON_PRINT_PDF_1, PRINT_PDF_REQUEST);
    public static final ConnectionTask PRINT_WEB_PAGE = new ConnectionTask(CONNECTION_AMAZON_PRINT_WEB_PAGE_1, PRINT_WEB_PAGE_REQUEST);

    public static class ConnectionTask {
        private String connectionType;
        private String requestType;

        private ConnectionTask(String connectionType, String requestType) {
            this.connectionType = connectionType;
            this.requestType = requestType;
        }

        public String getConnectionType() {
            return connectionType;
        }

        public String getRequestType() {
            return requestType;
        }
    }

}
