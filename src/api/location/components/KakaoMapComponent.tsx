import React, { useRef } from 'react';
import { View, StyleSheet } from 'react-native';
import { WebView } from 'react-native-webview';
import type { ShelterDto, DisasterDto } from '../types/Map';
import { KAKAO_JS_API_KEY } from '@env';

type Props = {
  latitude: number;
  longitude: number;
  shelters: ShelterDto[];
  disasters: DisasterDto[];
};

const KakaoMapView: React.FC<Props> = ({
  latitude,
  longitude,
  shelters,
  disasters,
}) => {
  const webViewRef = useRef(null);

  const injectedJavaScript = `
    const mapContainer = document.getElementById('map');
    const mapOption = {
      center: new kakao.maps.LatLng(${latitude}, ${longitude}),
      level: 4
    };
    const map = new kakao.maps.Map(mapContainer, mapOption);

    new kakao.maps.Marker({
      position: new kakao.maps.LatLng(${latitude}, ${longitude}),
      map: map,
      title: "현재 위치",
    });

    // 대피소 마커
    ${shelters
      .map(
        (shelter) => `
      new kakao.maps.Marker({
        map: map,
        position: new kakao.maps.LatLng(${shelter.latitude}, ${shelter.longitude}),
        title: "${shelter.name}",
        image: new kakao.maps.MarkerImage(
          'https://cdn-icons-png.flaticon.com/512/190/190411.png',
          new kakao.maps.Size(32, 32)
        )
      });
    `
      )
      .join('\n')}

    // 재난 마커
    ${disasters
      .map(
        (disaster) => `
      new kakao.maps.Marker({
        map: map,
        position: new kakao.maps.LatLng(${disaster.latitude}, ${disaster.longitude}),
        title: "${disaster.disasterType}",
        image: new kakao.maps.MarkerImage(
          'https://cdn-icons-png.flaticon.com/512/484/484167.png',
          new kakao.maps.Size(32, 32)
        )
      });
    `
      )
      .join('\n')}

    true;
  `;

  return (
    <View style={styles.container}>
      <WebView
        ref={webViewRef}
        originWhitelist={['*']}
        javaScriptEnabled
        injectedJavaScript={injectedJavaScript}
        source={{
          html: `
            <!DOCTYPE html>
            <html>
              <head>
                <meta charset="utf-8" />
                <meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0">
                <script src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${KAKAO_JS_API_KEY}"></script>
              </head>
              <body style="margin:0">
                <div id="map" style="width:100%;height:100vh;"></div>
              </body>
            </html>
          `,
        }}
        style={{ flex: 1 }}
      />
    </View>
  );
};

export default KakaoMapView;

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});
