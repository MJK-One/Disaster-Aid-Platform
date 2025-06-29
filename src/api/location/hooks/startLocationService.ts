import { NativeModules, Platform } from 'react-native';

const { IntentLauncher } = NativeModules;

const RECORDING_SERVICE_CLASS_NAME = 'com.disasteraidplatform.RecordingService';
const ACTION_START = "START";
const ACTION_STOP = "STOP";

export function startLocationTrackingService() {
  if (Platform.OS === 'android') {
    try {
      IntentLauncher.startService(RECORDING_SERVICE_CLASS_NAME, ACTION_START);
    } catch (e) {
      console.error('[startLocationTrackingService] error:', e);
    }
  } else {
    console.warn('startLocationTrackingService is only implemented on Android');
  }
}

export function stopLocationTrackingService() {
  if (Platform.OS === 'android') {
    try {
      IntentLauncher.startService(RECORDING_SERVICE_CLASS_NAME, ACTION_STOP);
    } catch (e) {
      console.error('[stopLocationTrackingService] error:', e);
    }
  } else {
    console.warn('stopLocationTrackingService is only implemented on Android');
  }
}
