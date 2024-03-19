/*Saya Muhammad Muhammad Fadlul Hafiizh [2209889] mengerjakan soal tp2 dalam mata kuliah DPBO.
untuk keberkahanNya maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan, Aamiin */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Menu extends JFrame{
    public static void main(String[] args) {
        // buat object window
        Menu window = new Menu();

        // atur ukuran window
        window.setSize(480, 560);
        // letakkan window di tengah layar
        window.setLocationRelativeTo(null);
        // isi window
        window.setContentPane(window.mainPanel);
        // ubah warna background
        window.getContentPane().setBackground(Color.white);
        // tampilkan window
        window.setVisible(true);
        // agar program ikut berhenti saat window diclose
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // index baris yang diklik
    private int selectedIndex = -1;
    // list untuk menampung semua mahasiswa
    private ArrayList<Mahasiswa> listMahasiswa;
    //objek dari database untuk konfigurasi koneksi
    private Database db;

    private JPanel mainPanel;
    private JTextField nimField;
    private JTextField namaField;
    private JTable mahasiswaTable;
    private JButton addUpdateButton;
    private JButton cancelButton;
    private JComboBox jenisKelaminComboBox;
    private JButton deleteButton;
    private JLabel titleLabel;
    private JLabel nimLabel;
    private JLabel namaLabel;
    private JLabel jenisKelaminLabel;
    private JRadioButton lunasRadioButton;
    private JRadioButton belumLunasRadioButton;

    private ButtonGroup isLunasUktBtnGroup = new ButtonGroup();

    // constructor
    public Menu() {
        // inisialisasi listMahasiswa
        listMahasiswa = new ArrayList<>();
        isLunasUktBtnGroup.add(lunasRadioButton);
        isLunasUktBtnGroup.add(belumLunasRadioButton);
        // instance dari koneksi database
        db = new Database();

        // isi tabel mahasiswa
        mahasiswaTable.setModel(setTable());

        // ubah styling title
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        // atur isi combo box
        String[] jenisKelaminData = {"", "Laki-laki", "Perempuan"};
        jenisKelaminComboBox.setModel(new DefaultComboBoxModel(jenisKelaminData));


        // sembunyikan button delete
        deleteButton.setVisible(false);

        // saat tombol add/update ditekan
        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex == -1){
                    insertData();
                }else{
                    updateData();
                }
            }
        });
        // saat tombol delete ditekan
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteData();
            }
        });
        // saat tombol cancel ditekan
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        // saat salah satu baris tabel ditekan
        mahasiswaTable.addMouseListener(
            new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    // ubah selectedIndex menjadi baris tabel yang diklik
                    selectedIndex = mahasiswaTable.getSelectedRow();

                    // simpan value textfield dan combo box
                    String selectedNim = mahasiswaTable.getModel().getValueAt(selectedIndex, 1).toString();
                    String selectedName = mahasiswaTable.getModel().getValueAt(selectedIndex, 2).toString();
                    String selectedJenisKelamin = mahasiswaTable.getModel().getValueAt(selectedIndex, 3).toString();
                    boolean is_lunas_ukt = mahasiswaTable.getModel().getValueAt(selectedIndex, 4) == "Lunas" ? true : false;

                    // ubah isi textfield dan combo box
                    nimField.setText(selectedNim);
                    namaField.setText(selectedName);
                    jenisKelaminComboBox.setSelectedItem(selectedJenisKelamin);
                    if (is_lunas_ukt == true){
                        lunasRadioButton.setSelected(true);
                    }else{
                        belumLunasRadioButton.setSelected(true);
                    }
                    // ubah button "Add" menjadi "Update"
                    addUpdateButton.setText("Update");
                    // tampilkan button delete
                    deleteButton.setVisible(true);
                }
        });
    }

    public final DefaultTableModel setTable() {
        // tentukan kolom tabel
        Object[] column = {"No", "NIM", "Nama", "Jenis Kelamin", "Status UKT"};

        // buat objek tabel dengan kolom yang sudah dibuat
        DefaultTableModel temp = new DefaultTableModel(null, column);

        try{
            ResultSet resultset = db.selectQuery("SELECT * FROM mahasiswa");
            listMahasiswa.clear();
            int i = 1;
            while(resultset.next()){
                Object[] row = new Object[5];
                row[0] = i;
                row[1] = resultset.getString("nim");
                row[2] = resultset.getString("nama");
                row[3] = resultset.getString("jenis_kelamin");
                row[4] = resultset.getBoolean("status_ukt") == false ? "Belum Lunas" : "Lunas";
                listMahasiswa.add(new Mahasiswa( //tiap row dari db yang ditampilkan pada table masukan juga pada list sehingga urutan dan isi list 1:1 dengan data yang ada di tampilan & db
                        resultset.getInt("id"),
                        resultset.getString("nim"),
                        resultset.getString("nama"),
                        resultset.getString("jenis_kelamin"),
                        resultset.getBoolean("status_ukt")
                ));
                temp.addRow(row);
                i++;
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return temp;
    }

    public void insertData() {
        // ambil value dari textfield dan combobox
        String nim = nimField.getText();
        String nama = namaField.getText();
        String jenisKelamin = jenisKelaminComboBox.getSelectedItem().toString();
        boolean is_lunas_ukt;
        if (lunasRadioButton.isSelected()){
            is_lunas_ukt = true;
        }else{
            is_lunas_ukt = false;
        }
        //cek semua field apakah sudah terisi & cek apakah nim yang diinputkan ada dalam list?
        if (!nim.equals("") && !nama.equals("") && !jenisKelamin.equals("") && isLunasUktBtnGroup.getSelection() != null && !listMahasiswa.stream().anyMatch(mhs -> nim.equals(mhs.getNim()))){
            // insert data kedalam db
            String sql = "INSERT INTO mahasiswa VALUES (null, '" + nim + "', '" + nama + "', '" + jenisKelamin + "', " + is_lunas_ukt + ")";
            db.insertUpdateDeleteQuery(sql);

            // update tabel
            mahasiswaTable.setModel(setTable());

            // bersihkan form
            clearForm();

            // feedback
            System.out.println("Insert berhasil!");
            JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan");
        }else{
            //validasi tiap filed yang kosong dan bila ada nim yang sama dalam list
            String messageAlert = "Tolong perhatikan!\n";
            if (nim.equals("")){
                System.out.println("hehe : " + nim);
                messageAlert += "Field nim wajib diisi\n";
            }
            if(listMahasiswa.stream().anyMatch(mhs -> nim.equals(mhs.getNim()))){
                messageAlert += "Field nim harus uniq\n";
            }
            if (nama.equals("")){
                messageAlert += "Field nama wajib diisi\n";
            }
            if (jenisKelamin.equals("")){
                messageAlert += "Field jenis kelamin wajib diisi\n";
            }
            if (isLunasUktBtnGroup.getSelection() == null){
                messageAlert += "Opsi status pembayaran ukt wajib dipilih\n";
            }
            JOptionPane.showMessageDialog(null, messageAlert);
        }
    }

    public void updateData() {
        // ambil data dari form
        String nim = nimField.getText();
        String nama = namaField.getText();
        String jenisKelamin = jenisKelaminComboBox.getSelectedItem().toString();
        boolean is_lunas_ukt;
        if (lunasRadioButton.isSelected()){
            is_lunas_ukt = true;
        }else{
            is_lunas_ukt = false;
        }
        //cek semua field apakah sudah terisi & cek apakah nim yang diinputkan ada dalam list?
        if (!nim.equals("") && !nama.equals("") && !jenisKelamin.equals("") && isLunasUktBtnGroup.getSelection() != null && !listMahasiswa.stream().anyMatch(mhs -> nim.equals(mhs.getNim()))){
//            update sql syntax
            String sql = "UPDATE mahasiswa SET " +
                    "nim = '" + nim + "', " +
                    "nama = '" + nama + "', " +
                    "jenis_kelamin = '" + jenisKelamin + "', " +
                    "status_ukt = " + is_lunas_ukt +
                    " WHERE id = " + listMahasiswa.get(selectedIndex).getId();
            System.out.println(sql);
            db.insertUpdateDeleteQuery(sql);

            // update tabel
            mahasiswaTable.setModel(setTable());

            // bersihkan form
            clearForm();

            // feedback
            System.out.println("Update berhasil");
            JOptionPane.showMessageDialog(null, "Data berhasil diubah");
        }else{
            //validasi tiap filed yang kosong dan bila ada nim yang sama dalam list
            String messageAlert = "Tolong perhatikan!\n";
            if (nim.equals("")){
                messageAlert += "Field nim wajib diisi\n";
            }
            if(listMahasiswa.stream().anyMatch(mhs -> nim.equals(mhs.getNim()))){
                messageAlert += "Field nim harus uniq\n";
            }
            if (nama.equals("")){
                messageAlert += "Field nama wajib diisi\n";
            }
            if (jenisKelamin.equals("")){
                messageAlert += "Field jenis kelamin wajib diisi\n";
            }
            if (isLunasUktBtnGroup.getSelection() == null){
                messageAlert += "Opsi status pembayaran ukt wajib dipilih\n";
            }
            JOptionPane.showMessageDialog(null, messageAlert);
        }
    }

    public void deleteData() {
        // hapus data dari db
        String sql = "DELETE FROM mahasiswa WHERE id = " +listMahasiswa.get(selectedIndex).getId();
        db.insertUpdateDeleteQuery(sql);

        // update tabel
        mahasiswaTable.setModel(setTable());

        // bersihkan form
        clearForm();

        // feedback
        System.out.println("Delete berhasil!");
        JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
    }

    public void clearForm() {
        // kosongkan semua texfield dan combo box
        nimField.setText("");
        namaField.setText("");
        jenisKelaminComboBox.setSelectedItem("");
        isLunasUktBtnGroup.clearSelection();

        // ubah button "Update" menjadi "Add"
        addUpdateButton.setText("Add");
        // sembunyikan button delete
        deleteButton.setVisible(true);
        // ubah selectedIndex menjadi -1 (tidak ada baris yang dipilih)
        selectedIndex = -1;
    }
}
