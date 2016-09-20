package io.apptik.rxhub;


import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"}, glue = "io.apptik.rxhub",
        features = {"src/test/resources/features/"})
public class BaseTest {
}
