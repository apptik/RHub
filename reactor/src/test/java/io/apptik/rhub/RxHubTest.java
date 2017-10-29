package io.apptik.rhub;


import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import reactor.core.publisher.DirectProcessor;

import static io.apptik.rhub.RxHubTest.Helper.getDummyConsumer;
import static org.assertj.core.api.Assertions.assertThat;

public class RxHubTest {

    Helper helper;

    public RxHubTest(Helper helper) {
        this.helper = helper;
    }

    @After
    public void complete() {

    }

    @Given("^Provider\"([^\"]*)\"$")
    public void provider(String provider) throws Throwable {
        helper.publishers.put(provider, DirectProcessor.create());
    }

    @Given("^Consumer\"([^\"]*)\"$")
    public void consumer(String consumer) throws Throwable {
        helper.consumers.put(consumer, getDummyConsumer());

    }

    @When("^Hub\"([^\"]*)\" subscribes to Provider\"([^\"]*)\" with tag \"([^\"]*)\"$")
    @Given("^Hub\"([^\"]*)\" is subscribed to Provider\"([^\"]*)\" with tag \"([^\"]*)\"$")
    public void hub_is_subscribed_to_Provider_with_tag(String hub, String provider,
                                                       String tag) throws Throwable {
        helper.hubs.get(hub).addUpstream(tag, helper.publishers.get(provider));
    }

    @When("^Consumer\"([^\"]*)\" subscribes to Hub\"([^\"]*)\" with tag \"([^\"]*)\"$")
    @Given("^Consumer\"([^\"]*)\" is subscribed to Hub\"([^\"]*)\" with tag \"([^\"]*)\"$")
    public void consumer_subscribes_to_Hub_with_tag(String consumer, String hub, String tag)
            throws Throwable {
        try {
            helper.hubs.get(hub).getPub(tag).subscribe(helper.consumers.get(consumer));
        } catch (Exception ex) {
            helper.error = ex;
        }
    }

    @When("^Consumer\"([^\"]*)\" subscribes to Hub\"([^\"]*)\" with tag \"([^\"]*)\" and filter\"" +
            "([^\"]*)\"$")
    @Given("^Consumer\"([^\"]*)\" is subscribed to Hub\"([^\"]*)\" with tag \"([^\"]*)\" and " +
            "filter\"([^\"]*)\"$")
    public void consumer_is_subscribed_to_Hub_with_tag_and_filter(
            String consumer, String hub, String tag, String filter) throws Throwable {
        try {

            helper.hubs.get(hub).getPub(tag, Class.forName(filter))
                    .subscribe(helper.consumers.get(consumer));

        } catch (Exception ex) {
            helper.error = ex;
        }
    }

    @When("^Provider\"([^\"]*)\" emits Event\"([^\"]*)\"$")
    public void provider_emits_Event(String provider, String event) throws Throwable {
        helper.publishers.get(provider).onNext(event);

    }

    @Then("^Consumer\"([^\"]*)\" should receive Event\"([^\"]*)\"$")
    public void consumer_should_receive_Event(final String consumer,final String event) throws Throwable {
        helper.consumers.get(consumer).await(Duration.ofSeconds(3),
                "consumer " + consumer + " should_receive_Event: " + event,
                new BooleanSupplier() {
                    @Override
                    public boolean getAsBoolean() {
                        return helper.consumers.get(consumer).values().contains(event);
                    }
                });
    }

    @Then("^Consumer\"([^\"]*)\" should not receive Event\"([^\"]*)\"$")
    public void consumer_should_not_receive_Event(String consumer, String event) throws Throwable {
        helper.consumers.get(consumer).assertDoesNotContainValue(event);
    }

    @Given("^Hub\"([^\"]*)\" with ProxyType ([^\\s]*) and Emittability \"([^\"]*)\"$")
    public void hub_with_ProxyType_(String hub, final String nodeType, final boolean emmitable)
            throws Throwable {
        helper.proxyType = nodeType;
        helper.hubs.put(hub, new AbstractReactorHub() {
            @Override
            public ProxyType getProxyType(Object tag) {
                return Helper.getProxyType(nodeType);
            }

            @Override
            public boolean canTriggerEmit(Object tag) {
                return emmitable;
            }
        });
    }

    @Given("^Hub\"([^\"]*)\" with ProxyType ([^\\s]*)$")
    public void hub_with_ProxyType_(String hub, final String nodeType)
            throws Throwable {
        helper.proxyType = nodeType;
        helper.hubs.put(hub, new AbstractReactorHub() {
            @Override
            public ProxyType getProxyType(Object tag) {
                return Helper.getProxyType(nodeType);
            }

            @Override
            public boolean canTriggerEmit(Object tag) {
                return true;
            }
        });
    }

    @When("^Event\"([^\"]*)\" with tag \"([^\"]*)\" is emitted on Hub\"([^\"]*)\"$")
    public void event_with_tag_is_emitted_on_Hub(String event, String tag, String hub) throws
            Throwable {
        try {
            helper.hubs.get(hub).emit(tag, event);
        } catch (Exception ex) {
            helper.error = ex;
        }
    }

    @When("^Provider\"([^\"]*)\" with tag \"([^\"]*)\" is removed from Hub\"([^\"]*)\"$")
    public void provider_with_tag_is_removed_from_Hub(String provider, String tag, String hub)
            throws
            Throwable {

        helper.hubs.get(hub).removeUpstream(tag, helper.publishers.get(provider));

    }

    @When("^providers are cleared from Hub\"([^\"]*)\"$")
    public void providers_are_cleared_from_Hub(String hub) throws Throwable {
        helper.hubs.get(hub).clearUpstream();
    }

    @Then("^there should be Error \"([^\"]*)\"$")
    public void there_should_be_Error(String error) throws Throwable {
        assertThat(helper.error).isNotNull();
        assertThat(helper.error.getClass().getName()).isEqualTo(error);
    }

    @Then("^there should be ErrorMessage \"([^\"]*)\"$")
    public void there_should_be_ErrorMessage(String errMsg) throws Throwable {
        assertThat(helper.error).isNotNull();
        assertThat(helper.error.getMessage()).isEqualTo(errMsg);
    }


    public static class Helper {
        public Map<String, AbstractReactorHub> hubs = new HashMap<>();
        public String proxyType;
        //use subjects  so we can easily emit events when needed
        public Map<String, DirectProcessor> publishers = new HashMap<>();
        public Map<String, AssertSubscriber> consumers = new HashMap<>();
        public Throwable error;

        public static AssertSubscriber getDummyConsumer() {
            System.err.println("GOT: getDummyConsumer");
            //return new DummyConsumer();
            return new AssertSubscriber();
        }

        public static RSHub.ProxyType getProxyType(String proxyType) {
            return ReactorProxyType.valueOf(proxyType);
        }
    }

}
