/*
 * Copyright 2002-2004 Jeremias Maerki.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.krysalis.barcode4j.cli;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.output.bitmap.BitmapEncoderRegistry;
import org.krysalis.barcode4j.output.eps.EPSCanvasProvider;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.tools.MimeTypes;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.krysalis.barcode4j.output.Orientation;

/**
 * Command-line interface.
 *
 * @author Jeremias Maerki
 * @author mk
 * @version 1.3
 */
public class Main {

    private static final String APP_HEADER = String.format("Barcode4J command-line application, Version %s%n", getVersion());

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    /**
     * stdout for this application (default: System.out)
     */
    private PrintStream stdout = System.out;
    /**
     * stderr for this application (default: System.err)
     */
    private PrintStream stderr = System.err;

    private ExitHandler exitHandler = new DefaultExitHandler();
    private Options options;
    private boolean headerPrinted = false;

    /**
     * Main method.
     *
     * @param args commandline arguments
     */
    public static void main(String... args) {
        final Main app = new Main();
        app.handleCommandLine(args);
    }

    public void setOutStream(PrintStream out) {
        stdout = out;
    }

    public void setErrStream(PrintStream err) {
        stderr = err;
    }

    /**
     * Set an alternative exit handler here.
     *
     * @param handler the alternative exit handler
     */
    public void setExitHandler(ExitHandler handler) {
        exitHandler = handler;
    }

    private CommandLine parseCommandLine(String... args) {
        CommandLine res = null;
        try {
            final CommandLineParser clp = new PosixParser();
            res = clp.parse(getOptions(), args);

            //Message
            String[] msg = res.getArgs();
            if (msg.length == 0) {
                throw new ParseException("No message");
            }
            if (msg.length > 1) {
                throw new ParseException("Too many parameters: " + msg.length);
            }
        } catch (MissingOptionException moe) {
            printHelp(new PrintWriter(stdout));
            exitHandler.failureExit(this,
                    "Bad command line. Missing option: " + moe.getMessage(), null, -2);
        } catch (ParseException pe) {
            printHelp(new PrintWriter(stdout));
            //pe.printStackTrace();
            exitHandler.failureExit(this,
                    "Bad command line: " + pe.getMessage(), null, -2);
        }
        return res;
    }

    /**
     * Handles the command line.
     *
     * The method calls the exit handler upon completion.
     *
     * @param args the command line arguments
     */
    public void handleCommandLine(String... args) {
        final CommandLine cl = parseCommandLine(args);
        if (cl == null) {
            return;
        }

        final String message = cl.getArgs()[0];

        OutputStream out = stdout;
        try {
            if (cl.hasOption("o")) {
                if (cl.hasOption('v')) {
                    Logger logger = Logger.getGlobal();
                    logger.setLevel(Level.ALL);
                }
                printAppHeader();
                final File outFile = new File(cl.getOptionValue("o"));
                LOGGER.log(Level.FINE, "Output to: {}", outFile.getCanonicalPath());
                out = new FileOutputStream(outFile);
            } else {
                printAppHeader();
            }

            LOGGER.log(Level.FINE, "Message: {}", message);

            //Output format
            final String format = MimeTypes.expandFormat(cl.getOptionValue("f", MimeTypes.MIME_SVG));
            LOGGER.log(Level.INFO, "Generating {}...", format);
            final Orientation orientation = Orientation.ZERO;
            final BarcodeUtil util = BarcodeUtil.getInstance();
            final BarcodeGenerator gen = util.createBarcodeGenerator(
                    getConfiguration(cl));

            final boolean renderRes;
            if (MimeTypes.MIME_SVG.equals(format)) {
                renderRes = renderSvg(message, gen, out, orientation);
            } else if (MimeTypes.MIME_EPS.equals(format)) {
                renderRes = renderEps(message, gen, out, orientation);
            } else {
                renderRes = renderBitmap(message, gen, out, orientation, format, Integer.parseInt(cl.getOptionValue('d', "300")), cl.hasOption("bw"));
            }

            if (!renderRes) {
                throw new IOException("Failure while writing BarcodeImage.");
            }
            LOGGER.info("done.");
            exitHandler.successfulExit(this);
        } catch (IOException ioe) {
            exitHandler.failureExit(this,
                    "Error writing output file: " + ioe.getMessage(), null, -5);
        } catch (IllegalArgumentException e) {
            exitHandler.failureExit(this,
                    "Error generating the barcode", e, -6);
        } catch (BarcodeException be) {
            exitHandler.failureExit(this,
                    "Error generating the barcode", be, -3);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                exitHandler.failureExit(this,
                        "Error writing output file: " + ex.getMessage(), null, -5);
            }
        }
    }

    private Options getOptions() {
        if (options == null) {
            this.options = new Options();

            OptionBuilder.withLongOpt("verbose");
            OptionBuilder.withDescription("enable debug output");
            this.options.addOption(OptionBuilder.create('v'));

            //Group: file/stdout
            OptionBuilder.withLongOpt("output");
            OptionBuilder.withArgName("file");
            OptionBuilder.hasArg();
            OptionBuilder.withDescription("the output filename");
            this.options.addOption(OptionBuilder.create('o'));

            //Group: config file/barcode type
            final OptionGroup group = new OptionGroup();
            group.setRequired(true);
            OptionBuilder.withArgName("file");
            OptionBuilder.withLongOpt("config");
            OptionBuilder.hasArg();
            OptionBuilder.withDescription("the config file");
            group.addOption(OptionBuilder.create('c'));

            OptionBuilder.withArgName("name");
            OptionBuilder.withLongOpt("symbol");
            OptionBuilder.hasArg();
            OptionBuilder.withDescription("the barcode symbology to select (default settings, use -c if you want to customize)");
            group.addOption(OptionBuilder.create('s'));
            this.options.addOptionGroup(group);

            //Output format type
            OptionBuilder.withArgName("format");
            OptionBuilder.withLongOpt("format");
            OptionBuilder.hasArg();
            OptionBuilder.withDescription(String.format("the output format: MIME type or file extension%nDefault: %s (SVG)", MimeTypes.MIME_SVG));
            this.options.addOption(OptionBuilder.create('f'));

            //Bitmap-specific options
            OptionBuilder.withArgName("integer");
            OptionBuilder.withLongOpt("dpi");
            OptionBuilder.hasArg();
            OptionBuilder.withDescription(String.format("(for bitmaps) the image resolution in dpi%nDefault: 300"));
            this.options.addOption(OptionBuilder.create('d'));

            OptionBuilder.withLongOpt("bw");
            OptionBuilder.withDescription("(for bitmaps) create monochrome (1-bit) image instead of grayscale (8-bit)");
            this.options.addOption(OptionBuilder.create());
        }
        return this.options;
    }

    private Configuration getConfiguration(CommandLine cl) throws BarcodeException {
        final Configuration res;
        if (cl.hasOption("s")) {
            res = ConfigurationBuilder.createDefaultConfiguration(cl.getOptionValue("s"));
        } else if (cl.hasOption("c")) {
            res = ConfigurationBuilder.buildFromFile(cl.getOptionValue("c"));
        } else {
            res = ConfigurationBuilder.createEmptyConfiguration();
        }
        return res;
    }

    /**
     * @return the Barcode4J version
     */
    public static String getVersion() {
        String version = null;
        final Package jarinfo = Main.class.getPackage();
        if (jarinfo != null) {
            version = jarinfo.getImplementationVersion();
        }
        if (version == null) {
            //Fallback if Barcode4J is used in a development environment
            version = "DEV";
        }
        return version;
    }

    /**
     * Prints the application header on the console.
     *
     * Ensures that this is only done once.
     */
    public void printAppHeader() {
        if (!headerPrinted) {
            LOGGER.info(APP_HEADER);
            headerPrinted = true;
        }
    }

    /**
     * Get a list of additional supported MIME types.
     *
     * @return Set of MIME-Types
     */
    private Set<String> computeAdditionalMimes() {
        final Set<String> additionalMimes = BitmapEncoderRegistry.getSupportedMIMETypes();
        additionalMimes.remove("");
        additionalMimes.remove(MimeTypes.MIME_PNG);
        additionalMimes.remove("image/png");
        additionalMimes.remove(MimeTypes.MIME_JPEG);
        additionalMimes.remove(MimeTypes.MIME_TIFF);
        additionalMimes.remove(MimeTypes.MIME_GIF);
        additionalMimes.remove(MimeTypes.MIME_BMP);
        return additionalMimes;
    }

    private void printHelp(PrintWriter writer) {
        printAppHeader();

        final HelpFormatter help = new HelpFormatter();
        help.printHelp(writer, HelpFormatter.DEFAULT_WIDTH,
                "java -jar barcode4j.jar "
                + "[-v] [[-s <symbology>]|[-c <cfg-file>]] [-f <format>] "
                + "[-d <dpi>] [-bw] [-o <file>] <message>",
                null,
                getOptions(),
                HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD,
                getAvailableFormatsHelp(computeAdditionalMimes()) + "\n\nIf -o is omitted the output is written to stdout.");
        writer.flush();
    }

    private String mimeTypeAvailable(String mimeType) {
        return BitmapEncoderRegistry.supports(mimeType) ? "" : " (unavailable)";
    }

    private String getAvailableFormatsHelp(Set<String> additionalMimes) {
        final String nl = System.getProperty("line.separator");
        final StringBuilder formats = new StringBuilder(300);
        formats.append(nl).append("Valid output formats:")
                .append(nl).append("SVG: " + MimeTypes.MIME_SVG + ", svg")
                .append(nl).append("EPS: " + MimeTypes.MIME_EPS + ", eps")
                .append(nl).append("PNG: " + MimeTypes.MIME_PNG + ", png").append(mimeTypeAvailable(MimeTypes.MIME_PNG))
                .append(nl).append("TIFF: " + MimeTypes.MIME_TIFF + ", tiff, tif").append(mimeTypeAvailable(MimeTypes.MIME_TIFF))
                .append(nl).append("JPEG: " + MimeTypes.MIME_JPEG + ", jpeg, jpg").append(mimeTypeAvailable(MimeTypes.MIME_JPEG))
                .append(nl).append("GIF: " + MimeTypes.MIME_GIF + ", gif").append(mimeTypeAvailable(MimeTypes.MIME_GIF))
                .append(nl).append("BMP: " + MimeTypes.MIME_BMP + ", bmp").append(mimeTypeAvailable(MimeTypes.MIME_BMP));

        if (!additionalMimes.isEmpty()) {
            formats.append(nl).append("Additional supported formats:").append(nl);
            for (final String mime : additionalMimes) {
                formats.append(mime).append(nl);
            }
        }
        return formats.toString();
    }

    private boolean renderBitmap(String message, BarcodeGenerator gen, OutputStream out, Orientation orientation, String format, int dpi, boolean bw) {
        final BitmapCanvasProvider bitmap;
        boolean res = true;
        final boolean antiAlias = !bw;
        final int imageType = bw ? BufferedImage.TYPE_BYTE_BINARY : BufferedImage.TYPE_BYTE_GRAY;
        LOGGER.log(Level.FINE, "Resolution: {}dpi", dpi);
        LOGGER.log(Level.FINE, bw ? "Black/white image (1-bit)" : "Grayscale image (8-bit) with anti-aliasing");
        LOGGER.log(Level.FINE, "AntiAlias: {}, ImageType: {}", new Object[]{antiAlias, imageType});

        bitmap = new BitmapCanvasProvider(out, format, dpi, imageType, antiAlias, orientation);
        gen.generateBarcode(bitmap, message);
        try {
            bitmap.finish();
        } catch (IOException e) {
            res = false;
            LOGGER.log(Level.SEVERE, "Error rendering Barcode in Bitmap format " + format + ".", e);
        }
        return res;
    }

    private boolean renderEps(String message, BarcodeGenerator gen, OutputStream out, Orientation orientation) {
        boolean res = true;
        try {
            final EPSCanvasProvider eps = new EPSCanvasProvider(out, orientation);
            gen.generateBarcode(eps, message);
            eps.finish();
        } catch (IOException e) {
            res = false;
            LOGGER.log(Level.SEVERE, "Error rendering Barcode in EPS format.", e);
        }
        return res;
    }

    private boolean renderSvg(String message, BarcodeGenerator gen, OutputStream out, Orientation orientation) {
        final SVGCanvasProvider svg = new SVGCanvasProvider(true, orientation);
        gen.generateBarcode(svg, message);
        try {
            TransformerFactory.newInstance().newTransformer()
                    .transform(new javax.xml.transform.dom.DOMSource(svg.getDOMFragment()), new javax.xml.transform.stream.StreamResult(out));
        } catch (TransformerException te) {
            exitHandler.failureExit(this, "XML/XSLT library error", te, -6);
        }
        return true;
    }
}
