
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNDatalogicScannerSpec.h"

@interface DatalogicScanner : NSObject <NativeDatalogicScannerSpec>
#else
#import <React/RCTBridgeModule.h>

@interface DatalogicScanner : NSObject <RCTBridgeModule>
#endif

@end
