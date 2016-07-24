package io.apptik.rxhub;


import cucumber.api.PendingException;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.it.Ma;
import rx.Observable;
import rx.Observer;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

import static io.apptik.rxhub.RxHubTest.Helper.getDummyConsumer;

public class RxHubTest {

    Helper helper;

    public RxHubTest(Helper helper) {
        this.helper = helper;
    }

    @Given("^Provider\"([^\"]*)\"$")
    public void provider(String provider) throws Throwable {
        helper.providers.put(provider, PublishSubject.create());
    }

    @Given("^Consumer\"([^\"]*)\"$")
    public void consumer(String consumer) throws Throwable {
        helper.consumers.put(consumer, getDummyConsumer());
    }

    @When("^Hub\"([^\"]*)\" subscribes to Provider\"([^\"]*)\" with tag \"([^\"]*)\"$")
    @Given("^Hub\"([^\"]*)\" is subscribed to Provider\"([^\"]*)\" with tag \"([^\"]*)\"$")
    public void hub_is_subscribed_to_Provider_with_tag(String hub, String provider,
                                                       String tag) throws Throwable {
        helper.hubs.get(hub).addProvider(tag, helper.providers.get(provider));
    }

    @When("^Consumer\"([^\"]*)\" subscribes to Hub\"([^\"]*)\" with tag \"([^\"]*)\"$")
    @Given("^Consumer\"([^\"]*)\" is subscribed to Hub\"([^\"]*)\" with tag \"([^\"]*)\"$")
    public void consumer_subscribes_to_Hub_with_tag(String consumer, String hub, String tag)
            throws Throwable {
        helper.hubs.get(hub).getNode(tag).subscribe(helper.consumers.get(consumer));
    }

    @When("^Provider\"([^\"]*)\" emits Event\"([^\"]*)\"$")
    public void provider_emits_Event(String provider, String event) throws Throwable {
        helper.providers.get(provider).onNext(event);
    }

    @Then("^Consumer\"([^\"]*)\" should receive Event\"([^\"]*)\"$")
    public void consumer_should_receive_Event(String consumer, String event) throws Throwable {
        assertThat(helper.consumers.get(consumer).events).contains(event);
    }

    @Then("^Consumer\"([^\"]*)\" should not receive Event\"([^\"]*)\"$")
    public void consumer_should_not_receive_Event(String consumer, String event) throws Throwable {
        assertThat(helper.consumers.get(consumer).events).doesNotContain(event);
    }


    @Given("^Hub\"([^\"]*)\" with NodeType (.*)$")
    public void hub_with_NodeType_ReplayRelay(String hub, final String nodeType) throws Throwable {
        helper.hubs.put(hub, new AbstractRxHub() {
            @Override
            public NodeType getNodeType(Object tag) {
                return NodeType.valueOf(nodeType);
            }

            @Override
            public boolean isNodeThreadsafe(Object tag) {
                return true;
            }
        });

    }

    @When("^Event\"([^\"]*)\" with tag \"([^\"]*)\" is emitted on Hub\"([^\"]*)\"$")
    public void event_with_tag_is_emitted_on_Hub(String event, String tag, String hub) throws
            Throwable {
        helper.hubs.get(hub).emit(tag, event);
    }

    @When("^Provider\"([^\"]*)\" with tag \"([^\"]*)\" is removed from Hub\"([^\"]*)\"$")
    public void provider_with_tag_is_removed_from_Hub(String provider, String tag, String hub)
            throws
            Throwable {
        helper.hubs.get(hub).removeProvider(tag, helper.providers.get(provider));
    }

    @When("^providers are cleared from Hub\"([^\"]*)\"$")
    public void providers_are_cleared_from_Hub(String hub) throws Throwable {
        helper.hubs.get(hub).clearProviders();
    }


    public static class Helper {
        public Map<String, RxHub> hubs = new HashMap<>();
        //use subjects  so we can easily emit events when needed
        public Map<String, PublishSubject> providers = new HashMap<>();
        public Map<String, DummyConsumer> consumers = new HashMap<>();

        public static DummyConsumer getDummyConsumer() {
            return new DummyConsumer();
        }
    }

    public static class DummyConsumer implements Observer<String> {
        ArrayList<String> events = new ArrayList<>();

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(String o) {
            events.add(o);
        }
    }


}
