package edu.rit.se.sars.operator.web.service;

import spark.Service;

public interface WebService {
    /**
     * Add HTTP routes to server
     * @param http Server to add routes to
     */
    void addRoutes(Service http);
}
