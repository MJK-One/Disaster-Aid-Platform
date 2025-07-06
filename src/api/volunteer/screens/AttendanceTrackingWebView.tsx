// AttendanceTrackingWebView.tsx
import React, { useRef, useEffect } from 'react';
import { View } from 'react-native';
import WebView, { WebViewMessageEvent } from 'react-native-webview';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useCurrentLocation } from '../../location/hooks/useCurrentLocation'; // 경로에 맞게 조정

const htmlContent = `
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>출석 실시간 알림 (WebView)</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      padding: 10px;
      background: #f0f0f0;
    }
    h1 {
      font-size: 20px;
      margin-bottom: 10px;
    }
    #log {
      white-space: pre-wrap;
      background: #fff;
      border: 1px solid #ccc;
      padding: 10px;
      height: 300px;
      overflow-y: scroll;
    }
    #status, #locationStatus {
      margin: 5px 0;
    }
    span {
      font-weight: bold;
    }
  </style>
</head>
<body>
  <h1>출석 실시간 알림 (Mobile)</h1>

  <div id="status">출석 WebSocket 상태: <span id="wsStatus">연결 전</span></div>
  <div id="locationStatus">위치 WebSocket 상태: <span id="locationWsStatus">연결 전</span></div>

  <pre id="log"></pre>

  <script>
    let token = null;
    let currentLatitude = null;
    let currentLongitude = null;
    let myVolunteerId = null;
    let trackingSocket = null;
    let locationSocket = null;

    const logEl = document.getElementById("log");
    const wsStatusEl = document.getElementById("wsStatus");
    const locationWsStatusEl = document.getElementById("locationWsStatus");

    function log(message) {
      const now = new Date().toLocaleTimeString();
      logEl.textContent += \`[\${now}] \${message}\\n\`;
      logEl.scrollTop = logEl.scrollHeight;
    }

    function sendLocation() {
      if (!myVolunteerId || currentLatitude == null || currentLongitude == null) {
        log("❌ 위치 정보 또는 volunteerId 누락");
        return;
      }
      if (locationSocket && locationSocket.readyState === WebSocket.OPEN) {
        const payload = {
          type: "location_update",
          data: {
            volunteerId: myVolunteerId,
            latitude: currentLatitude,
            longitude: currentLongitude
          }
        };
        locationSocket.send(JSON.stringify(payload));
        log(\`📡 위치 전송: 위도=\${currentLatitude}, 경도=\${currentLongitude}\`);
      }
    }

    function connectTrackingSocket() {
      if (!token) {
        log("❌ JWT 없음");
        return;
      }

      // Android 에뮬레이터 환경에서 localhost 대신 10.0.2.2 사용
      trackingSocket = new WebSocket(\`ws://10.0.2.2:8080/api/tracking?token=\${token}\`);

      trackingSocket.onopen = () => {
        wsStatusEl.textContent = "연결됨";
        log("✅ 출석 WebSocket 연결됨");
      };

      trackingSocket.onmessage = (e) => {
        const msg = JSON.parse(e.data);
        log(\`📩 출석 메시지: \${e.data}\`);

        if (msg.type === "READY") {
          myVolunteerId = msg.data.participantUserId;
          log(\`🆔 volunteerId 수신: \${myVolunteerId}\`);
        } else if (msg.type === "STARTED") {
          log("▶️ 출석 시작됨");
          requestLocation();
        } else if (msg.type === "ENDED") {
          log("🛑 출석 종료됨");
        } else if (msg.type === "tracking_result") {
          const r = msg.data;
          const isMine = myVolunteerId && r.volunteerId === myVolunteerId;
          log(\`🎯 출석 결과: \${r.volunteerId} → \${r.present ? "출석" : "결석"} \${isMine ? "(나)" : ""}\`);
        }
      };

      trackingSocket.onerror = () => {
        log("⚠️ 출석 WebSocket 오류 발생");
      };

      trackingSocket.onclose = () => {
        wsStatusEl.textContent = "연결 종료";
        log("🔌 출석 WebSocket 종료");
      };
    }

    function connectLocationSocket() {
      if (!token) return;

      locationSocket = new WebSocket(\`ws://10.0.2.2:8080/api/location-tracking?token=\${token}\`);

      locationSocket.onopen = () => {
        locationWsStatusEl.textContent = "연결됨";
        log("✅ 위치 WebSocket 연결됨");
      };

      locationSocket.onclose = () => {
        locationWsStatusEl.textContent = "연결 종료";
        log("🔌 위치 WebSocket 종료");
      };

      locationSocket.onerror = () => {
        log("⚠️ 위치 WebSocket 오류");
      };
    }

    function requestLocation() {
      window.ReactNativeWebView?.postMessage(JSON.stringify({ type: "GET_LOCATION" }));
    }

    window.addEventListener("message", (event) => {
      try {
        const msg = JSON.parse(event.data);

        if (msg.type === "JWT") {
          token = msg.payload;
          log("🔐 JWT 수신 완료");
          connectTrackingSocket();
          connectLocationSocket();
        }

        if (msg.type === "LOCATION") {
          const { latitude, longitude } = msg.payload;
          currentLatitude = latitude;
          currentLongitude = longitude;
          log(\`📍 위치 수신: \${latitude}, \${longitude}\`);
          sendLocation();
        }
      } catch (err) {
        log("⚠️ 메시지 파싱 실패");
      }
    });

    log("📲 WebView 준비 완료, JWT 요청 중...");
    window.ReactNativeWebView?.postMessage(JSON.stringify({ type: "REQUEST_JWT" }));
  </script>
</body>
</html>
`;

export default function AttendanceTrackingWebView() {
  const webviewRef = useRef<WebView>(null);
  const { latitude, longitude, loading } = useCurrentLocation();

  const onMessage = async (event: WebViewMessageEvent) => {
    try {
      const msg = JSON.parse(event.nativeEvent.data);

      if (msg.type === "REQUEST_JWT") {
        const token = await AsyncStorage.getItem("accessToken");
        webviewRef.current?.postMessage(JSON.stringify({ type: "JWT", payload: token }));
      }

      if (msg.type === "GET_LOCATION") {
        if (!loading && latitude != null && longitude != null) {
          webviewRef.current?.postMessage(
            JSON.stringify({ type: "LOCATION", payload: { latitude, longitude } })
          );
        }
      }
    } catch (error) {
      console.warn("WebView 메시지 처리 오류:", error);
    }
  };

  useEffect(() => {
    if (!loading && latitude != null && longitude != null) {
      webviewRef.current?.postMessage(
        JSON.stringify({ type: "LOCATION", payload: { latitude, longitude } })
      );
    }
  }, [latitude, longitude, loading]);

  return (
    <View style={{ flex: 1 }}>
      <WebView
        ref={webviewRef}
        originWhitelist={['*']}
        source={{ html: htmlContent }}
        onMessage={onMessage}
        javaScriptEnabled
        domStorageEnabled
      />
    </View>
  );
}
