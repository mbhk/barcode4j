Missing Features
================

BarcodeBean Configuration
-------------------------
Beans must be configurable like the "old" Objects.

Maybe using apache-commons:beanutils instead of avalon.

JSF Taglib
----------
Unified way to embed Barcodes in JSF Pages like
the one from Primefaces (p:barcode).
That one is broken after SVGCanvasProvider has been altered to
respect the "useNamespace" parameter. Additionaly it generates
QR-Codes using a different zxing-wrapper (net.glxn:qrgen).

Implement a ResourceHandlerWrapper to load the generated barcode.

After that the webapp should use this one and servlet code should
be removed (or marked deprecated).

Provide Message Validation
--------------------------
Every Barcode should provide a MessageValidator.
It must be possible to determine if a given message can be encoded
using the desired barcode-type.

Additionaly a suggestion could be made which barcodetypes are able to
handle the message.

Enhancements
============
* Reenable FOP and provide documentation and examples
* ~~Make Orientation more robust: 277 should be Orientation.TWOHUNDRED_SEVENTY~~
