package ggalantsev;

import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

public class Controller {

    private BufferedImage bufferedImage;
    private File fileChooserDir = null;
    private String comparedFileName;

    @FXML
    private Button setFirstPic;

    @FXML
    private Button setSecondPic;

    @FXML
    private Button compareButton;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private TextField firstPic;

    @FXML
    private TextField secondPic;

    @FXML
    private TextField deviation;

    @FXML
    private Slider deviationSlider;

    @FXML
    private Text wrongDeviationText;

    @FXML
    private TextField distance;

    @FXML
    private Slider distanceSlider;

    @FXML
    private Text wrongDistanceText;

    @FXML
    private CheckBox firstCheckBox;

    @FXML
    private CheckBox secondCheckBox;

    @FXML
    private Rectangle canvas;

    @FXML
    private ImageView comparedImage;

    @FXML
    private Button saveImageButton;

    @FXML
    private Button hideImageButton;

    @FXML
    public void setFirstPic() {
        String s = getImageAddressString();
        if (s != null) {
            firstPic.setText(s);
            fileChooserDir = new File(s.substring(0, s.lastIndexOf('\\')));
        }
    }

    @FXML
    public void setSecondPic() {
        String s = getImageAddressString();
        if (s != null) {
            secondPic.setText(s);
            fileChooserDir = new File(s.substring(0, s.lastIndexOf('\\')));
        }
    }

    private String getImageAddressString() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open image");
        if (fileChooserDir != null) {
            fileChooser.setInitialDirectory(fileChooserDir);
        }
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            return file.toString();
        } else {
            return null;
        }
    }

    @FXML
    public void compare() {
        double deviationVal;
        int distanceVal;

        try {
            deviationVal = Double.parseDouble(deviation.getText()) / 100;
            wrongDeviationText.setVisible(false);
            if (deviationVal < 0 || deviationVal > 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            wrongDeviationText.setVisible(true);
            return;
        }

        try {
            distanceVal = Integer.parseInt(distance.getText());
            wrongDistanceText.setVisible(false);
            if (distanceVal <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            wrongDistanceText.setVisible(true);
            return;
        }

        runCompareTask(deviationVal, distanceVal);

        setComparedPictureName();
    }

    private void runCompareTask(double deviationVal, int distanceVal) {
        progressIndicator.setVisible(true);
        compareButton.setText("");
        CompareImagesTask task = new CompareImagesTask(
                firstPic.getText(),
                secondPic.getText(),
                deviationVal,
                distanceVal,
                firstCheckBox.isSelected()
        );

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent t) {
                            bufferedImage = task.getValue();
                            showComparedImage();
                            progressIndicator.setVisible(false);
                            compareButton.setText("Compare!");
                    }
                });
    }

    private void showComparedImage() {
        WritableImage writableImage = SwingFXUtils.toFXImage(bufferedImage, null);
        comparedImage.setImage(writableImage);
        comparedImage.setVisible(true);
        saveImageButton.setVisible(true);
        hideImageButton.setVisible(true);
        canvas.setVisible(true);
    }

    private void setComparedPictureName() {
        if (secondCheckBox.isSelected()) {
            String s = secondPic.getText();
            s = s.substring(s.lastIndexOf('\\') + 1, s.length());
            comparedFileName = s.substring(0, s.lastIndexOf('.'))
                    .concat("_compared.png");
        } else {
            String s = firstPic.getText();
            s = s.substring(s.lastIndexOf('\\') + 1, s.length());
            comparedFileName = s.substring(0, s.lastIndexOf('.'))
                    .concat("_compared.png");
        }
    }

    @FXML
    private void saveImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        if (fileChooserDir != null) {
            fileChooser.setInitialDirectory(fileChooserDir);
        }
        fileChooser.setInitialFileName(comparedFileName);
        System.out.println(bufferedImage.getWidth());
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try {
                ImageIO.write(bufferedImage, "PNG", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void hideImageView() {
        comparedImage.setVisible(false);
        saveImageButton.setVisible(false);
        hideImageButton.setVisible(false);
        canvas.setVisible(false);
    }

    @FXML
    public void firstCheckBoxPressed() {
        if (firstCheckBox.isSelected()) {
            secondCheckBox.setSelected(!firstCheckBox.isSelected());
        } else {
            firstCheckBox.setSelected(true);
        }
    }

    @FXML
    public void secondCheckBoxPressed() {
        if (secondCheckBox.isSelected()) {
            firstCheckBox.setSelected(!secondCheckBox.isSelected());
        } else {
            secondCheckBox.setSelected(true);
        }
    }

    @FXML
    public void updateDeviationSlider() {
        try {
            deviationSlider.setValue(Double.parseDouble(deviation.getText()));
            wrongDeviationText.setVisible(false);
            deviationSlider.setVisible(true);
            if (Double.parseDouble(deviation.getText()) < 0
                    || Double.parseDouble(deviation.getText()) > 100) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            wrongDeviationText.setVisible(true);
            deviationSlider.setVisible(false);
            return;
        }
    }

    @FXML
    public void updateDistanceSlider() {
        try {
            distanceSlider.setValue(Integer.parseInt(distance.getText()));
            wrongDistanceText.setVisible(false);
            distanceSlider.setVisible(true);
            if (Integer.parseInt(distance.getText()) <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            wrongDistanceText.setVisible(true);
            distanceSlider.setVisible(false);
            return;
        }
    }

    @FXML
    public void updateDeviationTextField(){
        deviation.setText(
                String.format("%.1f",deviationSlider.getValue()));
    }

    @FXML
    public void updateDistanceTextField(){
        distance.setText(String.valueOf((int)distanceSlider.getValue()));
    }

    @FXML
    private void openGithub() {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/ggalantsev/"));
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

}
