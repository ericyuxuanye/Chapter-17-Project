package com.company;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

/**
 * Similar to FlatSVGIcon, but allows serialization without error
 */
public final class WritableSVGImage implements Icon, Serializable {

    private static final SVGUniverse svgUniverse = new SVGUniverse();

    private transient SVGDiagram diagram;
    private final int width;
    private final int height;
    private final double scale;
    private final char[] svgData;
    private final String filename;

    /**
     * Creates a new WritableImage, resized such that the height is the height
     * @param file the file
     * @param height height to be resized to
     */
    public WritableSVGImage(File file, int height) {
        filename = file.getName();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "File " + file.getName() + " not found",
                    "file not found",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        svgData = new char[(int) file.length()];
        for (int i = 0; i < svgData.length; i++) {
            try {
                svgData[i] = (char) br.read();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Error reading svg file: " + file.getName(),
                        "IOException",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        URI uri = svgUniverse.loadSVG(new CharArrayReader(svgData), file.getName(), false);
        diagram = svgUniverse.getDiagram(uri);
        scale = (double)height / diagram.getHeight();
        width = (int)(diagram.getWidth() * scale);
        this.height = height;
    }

    public WritableSVGImage(String resource, int height) {
        this(
                new File(toURI(Objects.requireNonNull(WritableSVGImage.class.getClassLoader().getResource(resource)))),
                height
        );
    }


    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        paintSVG((Graphics2D) g, x, y);
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

    private void paintSVGError(Graphics g, int x, int y) {
        g.setColor(Color.RED);
        g.fillRect(x, y, getIconWidth(), getIconHeight());
    }

    private void paintSVG(Graphics2D g, int x, int y) {
        // antialias
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        if (diagram == null) {
            paintSVGError(g, x, y);
            return;
        }
        g.translate(x, y);
        g.clipRect(0, 0, getIconWidth(), getIconHeight());
        g.scale(scale, scale);
        diagram.setIgnoringClipHeuristic(true);
        try {
            diagram.render(g);
        } catch (SVGException e) {
            paintSVGError(g, 0, 0);
        }
    }

    private static URI toURI(URL url) {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        URI uri = svgUniverse.loadSVG(new CharArrayReader(svgData), filename, false);
        diagram = svgUniverse.getDiagram(uri);
    }
}
