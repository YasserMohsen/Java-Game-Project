
import javafx.scene.image.Image;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;

import static javafx.scene.input.DataFormat.URL;
import javax.imageio.ImageIO;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author kazafy
 */
public class HandelImage {

    public void main(String[] args) {

        Image image = null;
        try {
            URL url = new URL("http://www.yahoo.com/image_to_read.jpg");

        String imageSource = "http://www.yahoo.com/image_to_read.jpg";
         
        ImageView imageView = ImageViewBuilder.create()
                .image(new Image(imageSource))
                .build();

            InputStream in = new BufferedInputStream(url.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            byte[] response = out.toByteArray();
            FileOutputStream fos = new FileOutputStream("C://borrowed_image.jpg");
            fos.write(response);
            fos.close();
        } catch (IOException e) {
        }

    }
}
