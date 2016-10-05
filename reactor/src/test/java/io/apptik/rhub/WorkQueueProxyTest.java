package io.apptik.rhub;


import cucumber.api.java.en.Then;

import static org.assertj.core.api.Assertions.assertThat;

public class WorkQueueProxyTest {


    RxHubTest.Helper helper;

    public WorkQueueProxyTest(RxHubTest.Helper helper) {
        this.helper = helper;
    }

    @Then("^Consumer\"([^\"]*)\" xor Consumer\"([^\"]*)\" should receive Event\"([^\"]*)\"$")
    public void consumer_xor_Consumer_should_receive_Event(String consumer1, String consumer2,
                                                           String event) throws Throwable {
        Thread.sleep(1000);

        boolean[] hasElement = new boolean[2];
        hasElement[0] = helper.consumers.get(consumer1).values().contains(event);
        hasElement[1] = helper.consumers.get(consumer2).values().contains(event);
        assertThat(hasElement).containsOnly(true, false);
    }


}
