package au.com.umranium.nodemcuwifi.presentation.tasks.connecting;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import javax.inject.Provider;

import au.com.umranium.nodemcuwifi.api.NodeMcuService;
import au.com.umranium.nodemcuwifi.api.ReceivedAccessPoint;
import au.com.umranium.nodemcuwifi.api.ReceivedAccessPoints;
import au.com.umranium.nodemcuwifi.presentation.common.ScannedAccessPoint;
import au.com.umranium.nodemcuwifi.presentation.common.Scheduler;
import au.com.umranium.nodemcuwifi.presentation.tasks.utils.WifiConnectionUtil;
import au.com.umranium.nodemcuwifi.wifievents.WifiConnected;
import au.com.umranium.nodemcuwifi.wifievents.WifiConnectivityEvent;
import au.com.umranium.nodemcuwifi.wifievents.WifiEvents;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

@RunWith(JMockit.class)
public class ConnectingControllerTest {

  @Injectable
  ConnectingController.Surface surface;
  @Injectable
  ScannedAccessPoint scannedAccessPoint;
  @Injectable
  WifiEvents wifiEvents;
  @Injectable
  WifiConnectionUtil wifiConnectionUtil;
  @Injectable
  Provider<NodeMcuService> serviceProvider;
  @Injectable
  NodeMcuService service;
  @Injectable
  Scheduler scheduler;
  @Mocked
  WifiConnected wifiConnected;
  @Tested
  ConnectingController controller;

  @Before
  public void setUp() throws Exception {
    new Expectations() {{
      serviceProvider.get();
      result = service;
      minTimes = 0;

      service.scan();
      result = Observable.just(createAccessPoints(new ReceivedAccessPoint("a", true, "0")));
      minTimes = 0;

      scheduler.mainThread();
      result = Schedulers.immediate();
      minTimes = 0;

      scheduler.io();
      result = Schedulers.immediate();
      minTimes = 0;
    }};
  }

  @Test
  public void whenAlreadyConnected_firesInitialEvent() throws Exception {
    // given:
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      result = true;
    }};

    // when:
    controller.onStart();

    // then:
    new Verifications() {{
      //noinspection unchecked
      surface.proceedToNextTask((List<ScannedAccessPoint>) any);
    }};
  }

  @Test
  public void whenNotConnected_doesNotFireEvent() throws Exception {
    // given:
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      result = false;
    }};

    // when:
    controller.onStart();

    // then:
    new Verifications() {{
      //noinspection unchecked
      surface.proceedToNextTask((List<ScannedAccessPoint>) any);
      times = 0;
    }};
  }

  @Test
  public void whenConnectedLater_firesEvent() throws Exception {
    final PublishSubject<WifiConnectivityEvent> connectivityEvents = PublishSubject.create();

    // given:
    new Expectations() {{
      //noinspection CheckResult
      wifiConnectionUtil.isAlreadyConnected();
      returns(false, false, true);

      wifiEvents.getConnectivityEvents();
      result = (Object) connectivityEvents;
    }};

    // when:
    controller.onStart();
    connectivityEvents.onNext(wifiConnected);

    // then:
    new Verifications() {{
      //noinspection unchecked
      surface.proceedToNextTask((List<ScannedAccessPoint>) any);
      times = 1;
    }};
  }

  private ReceivedAccessPoints createAccessPoints(ReceivedAccessPoint... accessPoints) {
    return new ReceivedAccessPoints(Arrays.asList(accessPoints));
  }
}