import { NativeModules, NativeEventEmitter } from 'react-native';

type Location = { latitude: number; longitude: number };

const { IntentLauncher } = NativeModules; // 네이티브에서 등록한 모듈 이름
const eventEmitter = new NativeEventEmitter(IntentLauncher);

/**
 * 네이티브에서 보낸 위치 데이터를 리스닝한다
 */
export function startLocationListener(callback: (location: Location) => void) {
  eventEmitter.addListener('onLocationUpdate', callback);
}
