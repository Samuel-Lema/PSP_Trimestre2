package main;

import java.awt.event.ActionEvent;

public class Administracion extends javax.swing.JFrame {

    public Administracion() {
        initComponents();
        
        this.setVisible(true);
        
        // Cierra la conexión con los cliente y cierra el Servidor
        btnExit.addActionListener((ActionEvent ae) -> {
            
            // Envia un mensaje a todos los clientes antes de cerrar el Servidor
            for (Sala sala: Sala.getSalas()) {
                
                sala.GlobalMensaje(null, " ### Se ha desconectado el Servidor ###", false);
            }
            
            System.exit(0);
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTab = new javax.swing.JTabbedPane();
        btnExit = new javax.swing.JButton();
        btnDrop = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnExit.setText("CERRAR");

        btnDrop.setText("ECHAR USUARIO");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTab))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(480, 480, 480)
                        .addComponent(btnExit, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btnDrop, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTab, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDrop, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(() -> {
            new Administracion().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected static javax.swing.JButton btnDrop;
    private javax.swing.JButton btnExit;
    public static javax.swing.JTabbedPane jTab;
    // End of variables declaration//GEN-END:variables
}
