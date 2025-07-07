import { NativeModules, Platform } from 'react-native';

const { IntentLauncher } = NativeModules;

const SERVICE_CLASS_NAME = 'com.disasteraidplatform.RecordingService';

type ServiceAction = 'START_TRACKING' | 'STOP_TRACKING' | 'START_SENDER' | 'STOP_SENDER' | 'START_FOREGROUND' | 'STOP_FOREGROUND';

function invokeService(action: ServiceAction) {
  if (Platform.OS !== 'android') {
    console.warn(`invokeService(${action}) only supported on Android`);
    return;
  }
  if (!IntentLauncher || typeof IntentLauncher.startService !== 'function') {
    console.error('IntentLauncher.startService is not available');
    return;
  }
  try {
    IntentLauncher.startService(SERVICE_CLASS_NAME, action);
  } catch (e) {
    console.error(`[invokeService] error during ${action}:`, e);
  }
}

export function startTrackingService() {
  invokeService('START_TRACKING');
}

export function stopTrackingService() {
  invokeService('STOP_TRACKING');
}

export function startLocationSenderService() {
  invokeService('START_SENDER');
}

export function stopLocationSenderService() {
  invokeService('STOP_SENDER');
}

export function startForegroundService() {
  invokeService('START_FOREGROUND');
}

export function stopForegroundService() {
  invokeService('STOP_FOREGROUND');
}
