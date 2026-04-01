package Cliente;

import modelos.Mensaje;
import modelos.PaqueteDatos;
import modelos.Usuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentanaCliente extends JFrame {

    private JTextArea areaChat;
    private JTextField campoMensaje;
    private JLabel labelEscribiendo;
    private ClienteConexion conexion;
    private Usuario usuarioActual; // El usuario que está usando esta ventana
    //componentes para el CRUD
    private JTextField txtID, txtUsername, txtNombreCompleto;
    private JComboBox<String> comboEstado;
    private JButton btnCrear, btnLeer, btnActualizar, btnEliminar;
    //componentes para la tabla del CRUD
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;

    public VentanaCliente() {
        configurarVentana();
        inicializarComponentes();
        // Iniciamos la conexión al servidor en un hilo secundario
        conexion = new ClienteConexion(this);
        new Thread(conexion).start();
    }

    private void configurarVentana() {
        setTitle("App de Chat Cliente-Servidor - " + usuarioActual.getUsername());
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        //pestañas para separar Chat y CRUD
        JTabbedPane pestañas = new JTabbedPane();

        // --- PESTAÑA CHAT ---
        JPanel panelChat = new JPanel(new BorderLayout());
        // Área central donde se ven los mensajes
        areaChat = new JTextArea();
        areaChat.setEditable(false);
        areaChat.setLineWrap(true);
        JScrollPane scrollChat = new JScrollPane(areaChat);
        panelChat.add(scrollChat, BorderLayout.CENTER);
        // Panel inferior para escribir mensajes
        JPanel panelInferior = new JPanel(new BorderLayout());
        // Leyenda sutil de escribiendo (Inicia oculta)
        labelEscribiendo = new JLabel(" ");
        labelEscribiendo.setForeground(Color.GRAY);
        labelEscribiendo.setFont(new Font("Arial", Font.ITALIC, 11));
        panelInferior.add(labelEscribiendo, BorderLayout.NORTH);
        campoMensaje = new JTextField();
        JButton btnEnviar = new JButton("Enviar");
        // Evento para el botón de enviar
        btnEnviar.addActionListener(e -> enviarMensaje());
        // Evento para enviar al presionar "Enter"
        campoMensaje.addActionListener(e -> enviarMensaje());
        // Evento para detectar cuándo el usuario teclea (Leyenda "escribiendo...")
        campoMensaje.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() != KeyEvent.VK_ENTER) {
                    enviarAvisoEscribiendo();
                }
            }
        });
        JPanel panelEntrada = new JPanel(new BorderLayout());
        panelEntrada.add(campoMensaje, BorderLayout.CENTER);
        panelEntrada.add(btnEnviar, BorderLayout.EAST);
        panelInferior.add(panelEntrada, BorderLayout.SOUTH);
        panelChat.add(panelInferior, BorderLayout.SOUTH);
        pestañas.addTab("Chat Principal", panelChat);

        // pestaña CRUD
        JPanel panelAdmin = crearPanelAdministracion();
        pestañas.addTab("Administración", panelAdmin);

        add(pestañas);
    }

    private void ejecutarAccionCRUD(String accion) {
        Usuario userCRUD = new Usuario();
        // Intentamos parsear el ID solo si no está vacío (para actualizar/eliminar)
        if (!txtID.getText().isEmpty()) {
            try {
                userCRUD.setIdUsuario(Integer.parseInt(txtID.getText()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El ID debe ser un número.");
                return;
            }
        }

        userCRUD.setUsername(txtUsername.getText());
        userCRUD.setNombreCompleto(txtNombreCompleto.getText());
        userCRUD.setEstado(comboEstado.getSelectedItem().toString());

        // Creamos el paquete con la acción correspondiente
        PaqueteDatos paquete = new PaqueteDatos(accion);
        paquete.setUsuario(userCRUD);
        // Enviamos al servidor
        conexion.enviarPaquete(paquete);
        // Limpiamos campos
        txtUsername.setText("");
        txtNombreCompleto.setText("");
    }

    private JPanel crearPanelAdministracion() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- FORMULARIO (Norte) ---
        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Etiquetas y Campos
        gbc.gridx = 0; gbc.gridy = 0;
        formulario.add(new JLabel("ID (Solo para Modificar/Eliminar):"), gbc);
        txtID = new JTextField(5);
        gbc.gridx = 1;
        formulario.add(txtID, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formulario.add(new JLabel("Username:"), gbc);
        txtUsername = new JTextField(15);
        gbc.gridx = 1;
        formulario.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formulario.add(new JLabel("Nombre Completo:"), gbc);
        txtNombreCompleto = new JTextField(15);
        gbc.gridx = 1;
        formulario.add(txtNombreCompleto, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formulario.add(new JLabel("Estado:"), gbc);
        comboEstado = new JComboBox<>(new String[]{"Conectado", "Desconectado", "Ausente"});
        gbc.gridx = 1;
        formulario.add(comboEstado, gbc);

        // --- BOTONES (Sur del Formulario) ---
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnCrear = new JButton("Registrar");
        btnLeer = new JButton("Consultar Todos");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");

        panelBotones.add(btnCrear);
        panelBotones.add(btnLeer);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formulario.add(panelBotones, gbc);

        panel.add(formulario, BorderLayout.NORTH);

        // Área de resultados del CRUD (Centro)
        String[] columnas = {"ID", "Username", "Nombre", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0); // 0 indica que inicia vacía
        tablaUsuarios = new JTable(modeloTabla);
        // Agregamos un listener para que al hacer clic en una fila, los datos suban al formulario
        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaUsuarios.getSelectedRow();
            if (fila != -1) {
                txtID.setText(modeloTabla.getValueAt(fila, 0).toString());
                txtUsername.setText(modeloTabla.getValueAt(fila, 1).toString());
                txtNombreCompleto.setText(modeloTabla.getValueAt(fila, 2).toString());
                comboEstado.setSelectedItem(modeloTabla.getValueAt(fila, 3).toString());
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaUsuarios);
        panel.add(scrollTabla, BorderLayout.CENTER); // Reemplaza al JTextArea anterior
        // --- EVENTOS DE LOS BOTONES ---
        btnCrear.addActionListener(e -> ejecutarAccionCRUD(PaqueteDatos.ACCION_CRUD_CREAR_USUARIO));
        btnLeer.addActionListener(e -> {
        // Para consultar no necesitamos llenar campos, solo pedir la lista
        PaqueteDatos paquete = new PaqueteDatos("CRUD_LEER_USUARIOS");
        conexion.enviarPaquete(paquete);
    });

btnActualizar.addActionListener(e -> {
    if(txtID.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Debes ingresar el ID del usuario a modificar.");
    } else {
        ejecutarAccionCRUD("CRUD_ACTUALIZAR_USUARIO");
    }
});

btnEliminar.addActionListener(e -> {
    if(txtID.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Debes ingresar el ID del usuario a eliminar.");
    } else {
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar este usuario?");
        if(confirm == JOptionPane.YES_OPTION) {
            ejecutarAccionCRUD("CRUD_ELIMINAR_USUARIO");
        }
    }
});

        return panel;
    }

    // Metodo que arma el paquete y lo manda al servidor
    private void enviarMensaje() {
        String texto = campoMensaje.getText().trim();
        if (!texto.isEmpty()) {
            // Obtenemos la hora actual
            String hora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            Mensaje nuevoMensaje = new Mensaje(0, usuarioActual.getIdUsuario(), texto, hora);

            PaqueteDatos paquete = new PaqueteDatos(PaqueteDatos.ACCION_CHAT);
            paquete.setMensaje(nuevoMensaje);
            paquete.setUsuario(usuarioActual); // para saber quién lo envía

            conexion.enviarPaquete(paquete);
            campoMensaje.setText(""); // Limpiamos el campo
        }
    }

    // Enviar paquete de "escribiendo..."
    private void enviarAvisoEscribiendo() {
        PaqueteDatos paquete = new PaqueteDatos(PaqueteDatos.ACCION_ESCRIBIENDO);
        paquete.setUsuario(usuarioActual);
        conexion.enviarPaquete(paquete);
    }

    // Metodos llamados por el hilo ClienteConexion

    public void mostrarNuevoMensaje(Mensaje m) {
        // Mostramos: [Hora] Usuario: Mensaje
        String msjFormateado = String.format("[%s] Usuario %d: %s\n", m.getFechaHora(), m.getIdUsuario(), m.getContenido());
        areaChat.append(msjFormateado);

        // Auto-scroll hacia abajo
        areaChat.setCaretPosition(areaChat.getDocument().getLength());
        // Borramos el aviso de "escribiendo..." si alguien mandó mensaje
        labelEscribiendo.setText(" ");
    }

    public void mostrarEscribiendo(String username) {
        // Evitamos que salga "está escribiendo..." en nuestra propia pantalla
        if (!username.equals(usuarioActual.getUsername())) {
            labelEscribiendo.setText(username + " está escribiendo...");

            // Usamos un Timer para ocultar el mensaje después de 2 segundos
            Timer timer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    labelEscribiendo.setText(" ");
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    public void actualizarListaUsuarios(List<Usuario> lista) {
        //Limpiamos la tabla para que no se dupliquen los datos
        modeloTabla.setRowCount(0);
        //Recorremos la lista que mandó el servidor y agregamos filas
        for (Usuario u : lista) {
            Object[] fila = {
                u.getIdUsuario(),
                u.getUsername(),
                u.getNombreCompleto(),
                u.getEstado()
            };
            modeloTabla.addRow(fila);
        }
    }


    public static void main(String[] args) {
        // Aseguramos que la GUI se inicie en el hilo correcto de Swing
        SwingUtilities.invokeLater(() -> {
            new VentanaCliente().setVisible(true);
        });
    }
}
