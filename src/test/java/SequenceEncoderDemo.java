

import io.humble.video.*;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SequenceEncoderDemo {

    public static void main(String[] args) throws IOException, InterruptedException {
        final int speed = 4;
        final int ballSize = 40;


        final Rational framerate = Rational.make(1, 25);
        final Muxer muxer = Muxer.make("./test.mp4", null, "mp4");
        final MuxerFormat format = muxer.getFormat();
        final Codec codec;
        codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());

        System.out.println("codec: " + codec);

        Encoder encoder = Encoder.make(codec);
        encoder.setWidth(1920);
        encoder.setHeight(1080);
        // We are going to use 420P as the format because that's what most video formats these days use
        final PixelFormat.Type pixelformat = PixelFormat.Type.PIX_FMT_YUV420P;
        encoder.setPixelFormat(pixelformat);
        encoder.setTimeBase(framerate);

        if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER))
            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);

        encoder.open(null, null);
        muxer.addNewStream(encoder);
        muxer.open(null, null);

        MediaPictureConverter converter = null;
        final MediaPicture picture = MediaPicture.make(
                encoder.getWidth(),
                encoder.getHeight(),
                pixelformat);
        picture.setTimeBase(framerate);

        System.out.println("quality: " + picture.getQuality());

        final MediaPacket packet = MediaPacket.make();

        int framesToEncode = 400;

        long totalNano = 0;
        BufferedImage image = new BufferedImage(1920, 1080, BufferedImage.TYPE_3BYTE_BGR);
        for (int i = 0, x = 0, y = 0, incX = speed, incY = speed; i < framesToEncode; i++, x += incX, y += incY) {
            Graphics g = image.getGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
            g.setColor(Color.YELLOW);
            if (x >= image.getWidth() - ballSize)
                incX = -speed;
            if (y >= image.getHeight() - ballSize)
                incY = -speed;
            if (x <= 0)
                incX = speed;
            if (y <= 0)
                incY = speed;
            g.fillOval(x, y, ballSize, ballSize);


            /** This is LIKELY not in YUV420P format, so we're going to convert it using some handy utilities. */
            if (converter == null)
                converter = MediaPictureConverterFactory.createConverter(image, picture);
            converter.toPicture(picture, image, i);

            long start = System.nanoTime();
            do {
                encoder.encode(packet, picture);
                if (packet.isComplete())
                    muxer.write(packet, false);
            } while (packet.isComplete());
            totalNano += System.nanoTime() - start;
        }

        do {
            encoder.encode(packet, null);
            if (packet.isComplete())
                muxer.write(packet,  false);
        } while (packet.isComplete());

        muxer.close();

        System.out.println("FPS: " + ((1000000000L * framesToEncode) / totalNano));
    }
}
