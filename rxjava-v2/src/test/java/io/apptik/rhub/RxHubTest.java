package io.apptik.rhub;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;

import static io.apptik.rhub.RxHubTest.Helper.getDummyConsumer;
import static org.assertj.core.api.Assertions.assertThat;

public class RxHubTest {

    Helper helper;

    public RxHubTest(Helper helper) {
        this.helper = helper;
    }

    @Given("^Provider\"([^\"]*)\"$")
    public void provider(String provider) throws Throwable {
        if (helper.isObservableType()) {
            helper.subjectProviders.put(provider, PublishSubject.create());
        } else {
            helper.processorProviders.put(provider, PublishProcessor.create());
        }
    }

    @Given("^Consumer\"([^\"]*)\"$")
    public void consumer(String consumer) throws Throwable {
        if (helper.isObservableType()) {
            helper.obsrvableConsumers.put(consumer, getDummyConsumer());
        } else {
            helper.publisherConsumers.put(consumer, new TestSubscriber());
        }
    }

    @When("^Hub\"([^\"]*)\" subscribes to Provider\"([^\"]*)\" with tag \"([^\"]*)\"$")
    @Given("^Hub\"([^\"]*)\" is subscribed to Provider\"([^\"]*)\" with tag \"([^\"]*)\"$")
    public void hub_is_subscribed_to_Provider_with_tag(String hub, String provider,
                                                       String tag) throws Throwable {
        if (helper.isObservableType()) {
            helper.subjHubs.get(hub).addUpstream(tag, helper.subjectProviders.get(provider));
        } else {
            helper.procHubs.get(hub).addUpstream(tag, helper.processorProviders.get(provider));
        }
    }

    @When("^Consumer\"([^\"]*)\" subscribes to Hub\"([^\"]*)\" with tag \"([^\"]*)\"$")
    @Given("^Consumer\"([^\"]*)\" is subscribed to Hub\"([^\"]*)\" with tag \"([^\"]*)\"$")
    public void consumer_subscribes_to_Hub_with_tag(String consumer, String hub, String tag)
            throws Throwable {
        try {
            if (helper.isObservableType()) {
                helper.subjHubs.get(hub).getPub(tag).subscribe(helper.obsrvableConsumers.get
                        (consumer));
            } else {
                helper.procHubs.get(hub).getPub(tag).subscribe(helper.publisherConsumers.get
                        (consumer));
            }
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
            if (helper.isObservableType()) {
                helper.subjHubs.get(hub).getPub(tag, Class.forName(filter))
                        .subscribe(helper.obsrvableConsumers.get(consumer));
            } else {
                helper.procHubs.get(hub).getPub(tag, Class.forName(filter))
                        .subscribe(helper.publisherConsumers.get(consumer));
            }
        } catch (Exception ex) {
            helper.error = ex;
        }
    }

    @When("^Provider\"([^\"]*)\" emits Event\"([^\"]*)\"$")
    public void provider_emits_Event(String provider, String event) throws Throwable {
        if (helper.isObservableType()) {
            helper.subjectProviders.get(provider).onNext(event);
        } else {
            helper.processorProviders.get(provider).onNext(event);
        }
    }

    @Then("^Consumer\"([^\"]*)\" should receive Event\"([^\"]*)\"$")
    public void consumer_should_receive_Event(String consumer, String event) throws Throwable {
        if (helper.isObservableType()) {
            assertThat(helper.obsrvableConsumers.get(consumer).events).contains(event);
        } else {
            assertThat(helper.publisherConsumers.get(consumer).values()).contains(event);
        }
    }

    @Then("^Consumer\"([^\"]*)\" should not receive Event\"([^\"]*)\"$")
    public void consumer_should_not_receive_Event(String consumer, String event) throws Throwable {
        if (helper.isObservableType()) {
            assertThat(helper.obsrvableConsumers.get(consumer).events).doesNotContain(event);
        } else {
            assertThat(helper.publisherConsumers.get(consumer).values()).doesNotContain(event);
        }
    }


    @Given("^Hub\"([^\"]*)\" with ProxyType ([^\\s]*) and Emittability \"([^\"]*)\"$")
    public void hub_with_ProxyType_(String hub, final String nodeType, final boolean emmitable)
            throws Throwable {
        helper.proxyType = nodeType;
        if (helper.isObservableType()) {
            helper.subjHubs.put(hub, new AbstractRxJava2ObsHub() {
                @Override
                public ProxyType getProxyType(Object tag) {
                    return Helper.getProxyType(nodeType);
                }

                @Override
                public boolean canTriggerEmit(Object tag) {
                    return emmitable;
                }
            });
        } else {
            helper.procHubs.put(hub, new AbstractRxJava2PubHub() {
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
    }

    @Given("^Hub\"([^\"]*)\" with ProxyType ([^\\s]*)$")
    public void hub_with_ProxyType_(String hub, final String nodeType)
            throws Throwable {
        helper.proxyType = nodeType;
        if (helper.isObservableType()) {
            helper.subjHubs.put(hub, new AbstractRxJava2ObsHub() {
                @Override
                public ProxyType getProxyType(Object tag) {
                    return Helper.getProxyType(nodeType);
                }

                @Override
                public boolean canTriggerEmit(Object tag) {
                    return true;
                }
            });
        } else {
            helper.procHubs.put(hub, new AbstractRxJava2PubHub() {
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
    }

    @When("^Event\"([^\"]*)\" with tag \"([^\"]*)\" is emitted on Hub\"([^\"]*)\"$")
    public void event_with_tag_is_emitted_on_Hub(String event, String tag, String hub) throws
            Throwable {
        try {
            if (helper.isObservableType()) {
                helper.subjHubs.get(hub).emit(tag, event);
            } else {
                helper.procHubs.get(hub).emit(tag, event);
            }
        } catch (Exception ex) {
            helper.error = ex;
        }
    }

    @When("^Provider\"([^\"]*)\" with tag \"([^\"]*)\" is removed from Hub\"([^\"]*)\"$")
    public void provider_with_tag_is_removed_from_Hub(String provider, String tag, String hub)
            throws
            Throwable {
        if (helper.isObservableType()) {
            helper.subjHubs.get(hub).removeUpstream(tag, helper.subjectProviders.get(provider));
        } else {
            helper.procHubs.get(hub).removeUpstream(tag, helper.processorProviders.get(provider));
        }
    }

    @When("^providers are cleared from Hub\"([^\"]*)\"$")
    public void providers_are_cleared_from_Hub(String hub) throws Throwable {
        if (helper.isObservableType()) {
            helper.subjHubs.get(hub).clearUpstream();
        } else {
            helper.procHubs.get(hub).clearUpstream();
        }
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
        public Map<String, RxJava2ObsHub> subjHubs = new HashMap<>();
        public Map<String, AbstractRxJava2PubHub> procHubs = new HashMap<>();
        public String proxyType;
        //use subjects  so we can easily emit events when needed
        public Map<String, PublishProcessor> processorProviders = new HashMap<>();
        public Map<String, PublishSubject> subjectProviders = new HashMap<>();
        public Map<String, DummyConsumer> obsrvableConsumers = new HashMap<>();
        public Map<String, TestSubscriber> publisherConsumers = new HashMap<>();
        public Throwable error;

        public static DummyConsumer getDummyConsumer() {
            return new DummyConsumer();
        }

        public boolean isObservableType() {
            return isObservableType(proxyType);
        }

        public static boolean isObservableType(String proxyType) {
            return proxyType.contains("Subject") || proxyType.contains("Observable")
                    || proxyType.contains("ObsSafe");
        }

        public static RSHub.ProxyType getProxyType(String proxyType) {
           if (isObservableType(proxyType)) {
                return RxJava2SubjProxyType.valueOf(proxyType);
            } else {
                return RxJava2ProcProxyType.valueOf(proxyType);
            }
        }
    }

    public static class DummyConsumer implements Observer<Object> {
        ArrayList<String> events = new ArrayList<>();

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onComplete() {
        }

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(Object o) {
            synchronized (this) {
                events.add(o.toString());
            }
        }
    }


}
