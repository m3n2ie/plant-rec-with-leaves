/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leafrecognition;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.*;
import org.opencv.utils.Converters;

/**
 *
 * @author Menzi Skhosana //216032734
 */
public class MainFrame extends javax.swing.JFrame {

    String filename = "";
    Mat imageMatrix = null;

    KNearest knn = null;
    List<Integer> testLabels = new ArrayList<Integer>();

    SVM svm = null;

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        this.setSize(575, 575);
        setLocationRelativeTo(null);

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        if (jRadioButton1.isSelected()) {
            jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Classification using K-Nearest Neighbour"));
        } else if (jRadioButton2.isSelected()) {
            jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Classification using Support Vector Machine"));
        }
        jLabel2.setText("");

        try {
            trainKNN(10, 20);
            trainSVM(10, 20, 5);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

    }

    void trainKNN(int plants, int leafs_per_plant) throws IOException {

        String url = "";
        Mat input = null;

        Mat trainData = new Mat();
        List<Integer> trainLabels = new ArrayList<Integer>();

        for (int i = 1; i <= plants; i++) {
            for (int j = 1; j <= leafs_per_plant; j++) {
                url = MainFrame.class.getResource("/leafrecognition/sorted/plant (" + i + ")/leaf (" + j + ").jpg").getPath().replace("%20", " ").substring(1);
                //System.out.println(url);
                input = Imgcodecs.imread(url, 0);
                Imgproc.resize(input, input, new Size(600, 800));
                input.convertTo(input, CvType.CV_32F);
                trainData.push_back(input.reshape(1, 1));
                trainLabels.add(i);

            }
        }

        knn = KNearest.create();
        knn.train(trainData, Ml.ROW_SAMPLE, Converters.vector_int_to_Mat(trainLabels));

    }

    void trainSVM(int plants, int leafs_per_plant, int num_of_features) throws IOException {

        ArrayList<Integer> tmp_labels = new ArrayList<>();
        ArrayList<Double> tmp_trainingData = new ArrayList<>();
        String url = "";

        for (int i = 1; i <= plants; i++) {
            for (int j = 1; j <= leafs_per_plant; j++) {
                tmp_labels.add(i);

                url = MainFrame.class.getResource("/leafrecognition/sorted/plant (" + i + ")/leaf (" + j + ").jpg").getPath().replace("%20", " ").substring(1);
                GLCMFeatureExtraction glcmfe = new GLCMFeatureExtraction(new File(url), 15);
                glcmfe.extract();

                tmp_trainingData.add(glcmfe.getContrast());
                tmp_trainingData.add(glcmfe.getHomogenity());
                tmp_trainingData.add(glcmfe.getEntropy());
                tmp_trainingData.add(glcmfe.getEnergy());
                tmp_trainingData.add(glcmfe.getDissimilarity());

            }
        }
        // .toArray(new Integer[objectArray.length]);

        int[] labels = new int[tmp_labels.size()];
        double[] trainingData = new double[tmp_trainingData.size()];

        for (int i = 0; i < tmp_labels.size(); i++) {
            labels[i] = tmp_labels.get(i);
        }

        for (int i = 0; i < tmp_trainingData.size(); i++) {
            trainingData[i] = tmp_trainingData.get(i);
        }

        Mat trainingDataMat = new Mat(plants * leafs_per_plant, num_of_features, CvType.CV_32FC1);
        trainingDataMat.put(0, 0, trainingData);
        Mat labelsMat = new Mat(plants * leafs_per_plant, 1, CvType.CV_32SC1);
        labelsMat.put(0, 0, labels);

        // Train the SVM
        //! [init]
        svm = SVM.create();
        svm.setType(SVM.C_SVC);
        svm.setKernel(SVM.LINEAR);
        svm.setTermCriteria(new TermCriteria(TermCriteria.MAX_ITER, 100, 1e-6));
        //! [init]
        //! [train]
        svm.train(trainingDataMat, Ml.ROW_SAMPLE, labelsMat);
        //! [train]

    }

    public String getPlantName(int plantId) {
        String[] plantNames = {"Abies Nordmanniana", "Acer Campestre", "Acer Ginnala", "Acer Negundo",
            "Acer Palmatum", "Albizia Julibrissin", "Betula Nigra", "Catalpa Speciosa", "Juglans Nigra",
            "Populus Deltoides"};

        if (plantId - 1 > 9 || plantId - 1 < 0) {
            return "Not Identified, " + plantId;
        }

        return plantNames[plantId - 1];
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup3 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Plant Classification using Leaf Recognition");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Image"));
        jPanel2.setLayout(new java.awt.GridLayout(1, 0));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel1);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Classification"));

        jPanel4.setLayout(new java.awt.GridLayout(1, 0));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("jLabel2");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 505, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Classification Technique"));

        buttonGroup3.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("K-Nearest Neighbours");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        buttonGroup3.add(jRadioButton2);
        jRadioButton2.setText("Support Vector Machine");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jRadioButton1)
                .addGap(63, 63, 63)
                .addComponent(jRadioButton2)
                .addGap(92, 92, 92))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jRadioButton1, jRadioButton2});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jRadioButton1, jRadioButton2});

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jMenu1.setText("File  ");

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Open Image");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem1.setText("Exit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("View");

        jMenuItem5.setText("View Test Results");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuItem4.setText("About");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Image");
            fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
            FileFilter filter = new FileNameExtensionFilter("Image files", "jpg", "png", "bmp");
            fileChooser.addChoosableFileFilter(filter);
            fileChooser.showOpenDialog(null);

            if (fileChooser.getSelectedFile() != null) {
                filename = fileChooser.getSelectedFile().toString();
                String extension = filename.substring(filename.length() - 4, filename.length());
                imageMatrix = Imgcodecs.imread(filename);
                MatOfByte matOfByte = new MatOfByte();
                Imgcodecs.imencode(extension, imageMatrix, matOfByte);

                byte[] byteArray = matOfByte.toArray();
                InputStream inputStream = new ByteArrayInputStream(byteArray);
                BufferedImage bufferedImage = ImageIO.read(inputStream);

                ImageIcon icon = new ImageIcon(bufferedImage);
                Image scaledImage = icon.getImage().getScaledInstance((int) (getBounds().width * 0.75), (int) (getBounds().height * 0.5), Image.SCALE_DEFAULT);

                jLabel1.setText("");
                jLabel1.setIcon(new ImageIcon(scaledImage));

                if (jRadioButton1.isSelected()) {
                    Mat test = Imgcodecs.imread(filename, 0);
                    Imgproc.resize(test, test, new Size(600, 800));
                    test.convertTo(test, CvType.CV_32F);
                    Mat testData = new Mat();
                    testData.push_back(test.reshape(1, 1));

                    Mat one_feature = testData.row(0);
                    Mat res = new Mat();
                    float p = knn.findNearest(one_feature, 1, res);
                    jLabel2.setText(getPlantName(Integer.parseInt(res.dump().replace("[", "").replace("]", ""))));

                } else if (jRadioButton2.isSelected()) {
//                    Mat bw = new Mat();
//                    bw = Imgcodecs.imread(fileChooser.getSelectedFile().toString(), 0);
//                    //Imgproc.cvtColor(srcOriginal, bw, Imgproc.COLOR_BGR2GRAY);
//
//                    Imgproc.threshold(bw, bw, 77, 255, Imgproc.THRESH_BINARY);
//
//                    HighGui.imshow("Binary Image", bw);

                    GLCMFeatureExtraction glcmfe = new GLCMFeatureExtraction(new File(filename), 15);
                    glcmfe.extract();

                    ArrayList<Double> tmp_testData = new ArrayList<>();

                    tmp_testData.add(glcmfe.getContrast());
                    tmp_testData.add(glcmfe.getHomogenity());
                    tmp_testData.add(glcmfe.getEntropy());
                    tmp_testData.add(glcmfe.getEnergy());
                    tmp_testData.add(glcmfe.getDissimilarity());

                    double[] testingData = new double[tmp_testData.size()];
                    for (int i = 0; i < tmp_testData.size(); i++) {
                        testingData[i] = tmp_testData.get(i);
                    }

                    Mat sampleMat = new Mat(1, 5, CvType.CV_32FC1);

                    sampleMat.put(0, 0, testingData);
                    jLabel2.setText(getPlantName(Math.round(svm.predict(sampleMat))));
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked

    }//GEN-LAST:event_jLabel1MouseClicked

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        if (jRadioButton1.isSelected()) {
            jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Classification using K-Nearest Neighbours"));

            try {
                Mat test = Imgcodecs.imread(filename, 0);
                Imgproc.resize(test, test, new Size(600, 800));
                test.convertTo(test, CvType.CV_32F);
                Mat testData = new Mat();
                testData.push_back(test.reshape(1, 1));

                Mat one_feature = testData.row(0);
                Mat res = new Mat();
                float p = knn.findNearest(one_feature, 1, res);
                jLabel2.setText(getPlantName(Integer.parseInt(res.dump().replace("[", "").replace("]", ""))));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "No Image Loaded");
            }

        } else if (jRadioButton2.isSelected()) {
            jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Classification using Support Vector Machine"));

            GLCMFeatureExtraction glcmfe = null;
            try {
                glcmfe = new GLCMFeatureExtraction(new File(filename), 15);
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (glcmfe != null) {
                glcmfe.extract();

                ArrayList<Double> tmp_testData = new ArrayList<>();

                tmp_testData.add(glcmfe.getContrast());
                tmp_testData.add(glcmfe.getHomogenity());
                tmp_testData.add(glcmfe.getEntropy());
                tmp_testData.add(glcmfe.getEnergy());
                tmp_testData.add(glcmfe.getDissimilarity());

                double[] testingData = new double[tmp_testData.size()];
                for (int i = 0; i < tmp_testData.size(); i++) {
                    testingData[i] = tmp_testData.get(i);
                }

                Mat sampleMat = new Mat(1, 5, CvType.CV_32FC1);

                sampleMat.put(0, 0, testingData);
                jLabel2.setText(getPlantName(Math.round(svm.predict(sampleMat))));
            }
        }

    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        if (jRadioButton1.isSelected()) {
            jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Classification using K-Nearest Neighbours"));

            Mat test = Imgcodecs.imread(filename, 0);
            Imgproc.resize(test, test, new Size(600, 800));
            test.convertTo(test, CvType.CV_32F);
            Mat testData = new Mat();
            testData.push_back(test.reshape(1, 1));

            Mat one_feature = testData.row(0);
            Mat res = new Mat();
            float p = knn.findNearest(one_feature, 1, res);
            jLabel2.setText(getPlantName(Integer.parseInt(res.dump().replace("[", "").replace("]", ""))));

        } else if (jRadioButton2.isSelected()) {
            jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Classification using Support Vector Machine"));

            GLCMFeatureExtraction glcmfe = null;
            try {
                glcmfe = new GLCMFeatureExtraction(new File(filename), 15);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "No Image Loaded");
            }

            if (glcmfe != null) {
                glcmfe.extract();

                ArrayList<Double> tmp_testData = new ArrayList<>();

                tmp_testData.add(glcmfe.getContrast());
                tmp_testData.add(glcmfe.getHomogenity());
                tmp_testData.add(glcmfe.getEntropy());
                tmp_testData.add(glcmfe.getEnergy());
                tmp_testData.add(glcmfe.getDissimilarity());

                double[] testingData = new double[tmp_testData.size()];
                for (int i = 0; i < tmp_testData.size(); i++) {
                    testingData[i] = tmp_testData.get(i);
                }

                Mat sampleMat = new Mat(1, 5, CvType.CV_32FC1);

                sampleMat.put(0, 0, testingData);
                jLabel2.setText(getPlantName(Math.round(svm.predict(sampleMat))));
            }
        }

    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        new About().setVisible(true);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        String file_path = "";
        String[] plants = {"Abies Nordmanniana", "Acer Campestre", "Acer Ginnala", "Acer Negundo",
            "Acer Palmatum", "Albizia Julibrissin", "Betula Nigra", "Catalpa Speciosa", "Juglans Nigra",
            "Populus Deltoides"};
        int[] num_of_testing_leafs = {35, 36, 30, 30, 87, 27, 20, 71, 21, 45};

        int count_knn = 0;
        int count_svm = 0;
        int count_total = 0;
        String knn_result = "";
        String svm_result = "";
        
        int [] svm_leaf_count = {0,0,0,0,0,0,0,0,0,0};
        int [] knn_leaf_count = {0,0,0,0,0,0,0,0,0,0};
        
        int count = 0;

        for (int i=1; i<=10; i++) {
            String p_name = plants[i-1];
            for (int j = 1; j<=num_of_testing_leafs[i-1]; j++) {
                count_total++;
                //file_path = "\\leafrecognition\\testing";
//                MainFrame.class.getResource("/leafrecognition/sorted/plant (" + i + ")/leaf (" + j + ").jpg").getPath().replace("%20", " ").substring(1);
//                file_path += "\\"+p_name.toLowerCase().replace(" ", "_") + "\\1 ("+j+").jpg";
                file_path = MainFrame.class.getResource("/leafrecognition/testing/"+p_name.toLowerCase().replace(" ", "_") + "/1 ("+j+").jpg").getPath().replace("%20", " ").substring(1);
                
                System.out.println("Please wait... \n"+file_path);
                
                //KNN
                Mat test = Imgcodecs.imread(file_path, 0);
                Imgproc.resize(test, test, new Size(600, 800));
                test.convertTo(test, CvType.CV_32F);
                Mat testData = new Mat();
                testData.push_back(test.reshape(1, 1));

                Mat one_feature = testData.row(0);
                Mat res = new Mat();
                float p = knn.findNearest(one_feature, 1, res);
                knn_result = getPlantName(Integer.parseInt(res.dump().replace("[", "").replace("]", "")));

                if (knn_result.equals(p_name)) {
                    count_knn++;
                    knn_leaf_count[i-1] ++;
                }

                //SVM
                GLCMFeatureExtraction glcmfe = null;
                try {
                    glcmfe = new GLCMFeatureExtraction(new File(file_path), 15);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "No Image Loaded");
                }

                if (glcmfe != null) {
                    glcmfe.extract();

                    ArrayList<Double> tmp_testData = new ArrayList<>();

                    tmp_testData.add(glcmfe.getContrast());
                    tmp_testData.add(glcmfe.getHomogenity());
                    tmp_testData.add(glcmfe.getEntropy());
                    tmp_testData.add(glcmfe.getEnergy());
                    tmp_testData.add(glcmfe.getDissimilarity());

                    double[] testingData = new double[tmp_testData.size()];
                    for (int k = 0; k < tmp_testData.size(); k++) {
                        testingData[k] = tmp_testData.get(k);
                    }

                    Mat sampleMat = new Mat(1, 5, CvType.CV_32FC1);

                    sampleMat.put(0, 0, testingData);
                    svm_result = getPlantName(Math.round(svm.predict(sampleMat)));
                }
                
                if (svm_result.equals(p_name)) {
                    count_svm++;
                    svm_leaf_count[i-1] ++;
                }
            }
        }
        
        System.out.println("=================================================");
        
        for(int i = 1; i<=10 ; i++){
            System.out.println(getPlantName(i)+" "+svm_leaf_count[i-1]+" "+knn_leaf_count[i-1]);
        }
        
        System.out.println("Leaves Identified with KNN : "+count_knn+"\nLeaves Identified with SVM :"+count_svm+"\nTotal Leaves Tested : "+count_total);

    }//GEN-LAST:event_jMenuItem5ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    // End of variables declaration//GEN-END:variables
}
