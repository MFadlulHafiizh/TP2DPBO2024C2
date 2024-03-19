/*Saya Muhammad Muhammad Fadlul Hafiizh [2209889] mengerjakan soal tp2 dalam mata kuliah DPBO.
untuk keberkahanNya maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan, Aamiin */
import java.sql.*;

public class Database {
    private Connection connection;
    private Statement statement;

    public Database() {
        try{
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_mahasiswa_dpbo", "root", null);
            statement = connection.createStatement();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public ResultSet selectQuery(String sql){
        try{
            statement.execute(sql);
            return  statement.getResultSet();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public int insertUpdateDeleteQuery(String sql){
        try {
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Statement getStatement() {
        return statement;
    }
}
