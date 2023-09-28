import { NativeModules } from 'react-native';

type DatalogicScannerType = {
  testScan(success: boolean): Promise<string>;
  scanOnce(): Promise<string>;
  startScanning(): Promise<string>;
  stopScanning(): void;
  unlockFromCradle(): Promise<boolean>;
  getCradleState(): Promise<string>;
  listenToCradle(): void;
};

const { DatalogicScanner } = NativeModules;

export default DatalogicScanner as DatalogicScannerType;
