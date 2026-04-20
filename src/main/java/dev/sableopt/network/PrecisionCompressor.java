package dev.sableopt.network;

import io.netty.buffer.ByteBuf;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class PrecisionCompressor {

    public static void writeCompressedPosition(ByteBuf buf, Vector3dc pos) {
        writeHalfFloat(buf, (float) pos.x());
        writeHalfFloat(buf, (float) pos.y());
        writeHalfFloat(buf, (float) pos.z());
    }

    public static void writeCompressedVelocity(ByteBuf buf, Vector3fc vel) {
        writeHalfFloat(buf, vel.x());
        writeHalfFloat(buf, vel.y());
        writeHalfFloat(buf, vel.z());
    }

    public static Vector3f readCompressedPosition(ByteBuf buf) {
        float x = readHalfFloat(buf);
        float y = readHalfFloat(buf);
        float z = readHalfFloat(buf);
        return new Vector3f(x, y, z);
    }

    public static Vector3f readCompressedVelocity(ByteBuf buf) {
        float x = readHalfFloat(buf);
        float y = readHalfFloat(buf);
        float z = readHalfFloat(buf);
        return new Vector3f(x, y, z);
    }

    private static void writeHalfFloat(ByteBuf buf, float value) {
        int half = floatToHalf(value);
        buf.writeShort(half);
    }

    private static float readHalfFloat(ByteBuf buf) {
        int half = buf.readShort();
        return halfToFloat(half);
    }

    private static int floatToHalf(float f) {
        int bits = Float.floatToIntBits(f);
        int sign = bits >>> 31;
        int exp = (bits >>> 23) & 0xFF;
        int mant = bits & 0x7FFFFF;

        if (exp == 0) {
            return sign << 15;
        }
        if (exp == 255) {
            return (sign << 15) | 0x7FFF;
        }

        int newExp = exp - 127 + 15;
        if (newExp <= 0) {
            return sign << 15;
        }
        if (newExp >= 31) {
            return (sign << 15) | 0x7FFF;
        }

        int newMant = mant >> 13;
        return (sign << 15) | (newExp << 10) | newMant;
    }

    private static float halfToFloat(int half) {
        int sign = half >>> 15;
        int exp = (half >>> 10) & 0x1F;
        int mant = half & 0x3FF;

        if (exp == 0) {
            if (mant == 0) {
                return sign == 0 ? 0.0f : -0.0f;
            }
            exp = 1;
            while ((mant & 0x400) == 0) {
                mant <<= 1;
                exp--;
            }
            mant &= 0x3FF;
            exp += 127 - 15;
        } else if (exp == 31) {
            return sign == 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
        } else {
            exp += 127 - 15;
        }

        int bits = (sign << 31) | (exp << 23) | (mant << 13);
        return Float.intBitsToFloat(bits);
    }
}
