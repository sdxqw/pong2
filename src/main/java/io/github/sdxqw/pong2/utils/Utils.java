package io.github.sdxqw.pong2.utils;

import org.lwjgl.BufferUtils;
import org.lwjgl.nanovg.NVGColor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Utils {
    public static ByteBuffer readFile(Path path) throws IOException {
        try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ)) {
            long fileSize = fc.size();
            if (fileSize == 0) {
                throw new IOException("File is empty: " + path);
            }

            ByteBuffer buffer = BufferUtils.createByteBuffer((int) fileSize);
            fc.read(buffer);
            buffer.flip();
            return buffer;
        }
    }

    public static ByteBuffer stringToByteBuffer(String string, ByteBuffer buffer) {
        for (int i = 0; i < string.length(); i++) {
            buffer.put((byte) string.charAt(i));
        }
        buffer.put((byte) 0);
        return buffer;
    }

    public static NVGColor color(float r, float g, float b, float a) {
        return NVGColor.calloc().a(a).r(r).g(g).b(b);
    }
}
