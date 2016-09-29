package io.apptik.rhub;


import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"}, glue = "io.apptik.rhub",
        features = {"src/test/resources/features/"},
        format = {"html:./build/cuc-report/html"})
public class BaseTest {
}
