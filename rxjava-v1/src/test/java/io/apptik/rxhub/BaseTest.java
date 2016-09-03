package io.apptik.rxhub;


import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"}, glue = "io.apptik.rxhub",
        features = {"src/test/resources/features/"})
public class BaseTest {
}
