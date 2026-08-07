package com.clinic.booking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "clinic_audit_db";
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}
