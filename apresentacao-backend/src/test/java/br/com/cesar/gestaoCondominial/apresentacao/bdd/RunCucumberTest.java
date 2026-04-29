package br.com.cesar.gestaoCondominial.apresentacao.bdd;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.FEATURES_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "br.com.cesar.gestaoCondominial.apresentacao.bdd")
@ConfigurationParameter(key = FEATURES_PROPERTY_NAME, value = "classpath:features")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports.html, json:target/cucumber-reports.json, junit:target/cucumber-reports.xml")
public class RunCucumberTest {
}
