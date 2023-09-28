package com.datalogicscanner

import com.datalogic.decode.BarcodeManager
import com.datalogic.decode.DecodeException
import com.datalogic.decode.ReadListener
import com.datalogic.decode.configuration.ScannerProperties
import com.datalogic.device.ErrorManager

import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule

class DatalogicScannerModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  private var barcodeManagerOnce: BarcodeManager? = null
  private var listenerOnce: ReadListener? = null
  private var barcodeManagerContinuous: BarcodeManager? = null
  private var listenerContinuous: ReadListener? = null

  override fun getName(): String {
    return NAME
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun multiply(a: Double, b: Double, promise: Promise) {
    promise.resolve(a * b)
  }

  companion object {
    const val NAME = "DatalogicScanner"
  }

  private fun emitBarcode(barcode: String) {
    val params = Arguments.createMap()
    params.putString("barcode", barcode)
    reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit("BarcodeScanned", params)
  }
 @ReactMethod
  fun testScan(success: Boolean, promise: Promise) {
    if (success) {
      promise.resolve("TestScan Success")
    } else {
      promise.reject(Exception("TestScan Failure"))
    }
  }

  @ReactMethod
  fun scanOnce(promise: Promise) {
    try {
      barcodeManagerOnce = BarcodeManager().apply {
        setScannerProperties(this)
      }
      ErrorManager.enableExceptions(true)
      listenerOnce = ReadListener { decodeResult ->
        promise.resolve(decodeResult.text)
        barcodeManagerOnce?.removeReadListener(listenerOnce)
        barcodeManagerOnce = null
      }
      val added = barcodeManagerOnce!!.addReadListener(listenerOnce)
    } catch (de: DecodeException) {
      promise.reject(de)
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  fun startScanning(promise: Promise) {
    if (barcodeManagerContinuous != null) {
      return
    }

    try {
      barcodeManagerContinuous = BarcodeManager().apply {
        setScannerProperties(this)
      }
      ErrorManager.enableExceptions(true)
      listenerContinuous = ReadListener { decodeResult ->
        emitBarcode(decodeResult.text)
        promise.resolve(decodeResult.text)
      }
      barcodeManagerContinuous!!.addReadListener(listenerContinuous)
    } catch (de: DecodeException) {
      promise.reject(de)
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  fun stopScanning() {
    if (barcodeManagerContinuous == null) {
      return
    }

    if (listenerContinuous != null) {
      barcodeManagerContinuous!!.removeReadListener(listenerContinuous)
      listenerContinuous = null
    }

    barcodeManagerContinuous = null
  }
  private fun setScannerProperties(barcodeManager: BarcodeManager) {
    ScannerProperties.edit(barcodeManager).apply {
      ean13.sendChecksum.set(true)
      code39.code32.set(false)
      goodread.goodReadEnable.set(true)
      displayNotification.enable.set(false)
      store(barcodeManager, true)
    }
  }
}
