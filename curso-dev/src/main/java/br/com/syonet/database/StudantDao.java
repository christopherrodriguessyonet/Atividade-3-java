package br.com.syonet.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import br.com.syonet.model.Studant;
import br.com.syonet.model.StudantRepository;

public class StudantDao implements StudantRepository {
  private final Logger log = Logger.getLogger(this.getClass().getName());
  private Connection connection;

	public StudantDao(Connection connection) {
    this.connection = connection;
  }

	@Override
	public Studant create(Studant studant) {
    String sql = "insert into students (name, age, email) values (?, ?, ?);";
    try (PreparedStatement prst = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			prst.setString(1, studant.getName());
      prst.setInt(2, studant.getAge());
      prst.setString(3, studant.getEmail());
      prst.executeUpdate();
      try (ResultSet resultSet = prst.getGeneratedKeys()) {
        resultSet.next();
        int id = resultSet.getInt(1);
        return new Studant(id, studant.getName(), studant.getAge(), studant.getEmail());
      }
		} catch (SQLException e) {
			log.warning(e.getMessage());
      throw new RuntimeException(e);
		}
	}

  @Override
  public List<Studant> listAll() {
    try (Statement st = this.connection.createStatement()) {
      st.execute("SELECT id, name, age, email FROM students;");
      List<Studant> result = new ArrayList<>();
      try (ResultSet rs = st.getResultSet()) {
        while (rs.next()) {
          var id = rs.getInt("id");
          var name = rs.getString("name");
          var age = rs.getInt("age");
          var email = rs.getString("email");
          result.add(new Studant(id, name, age, email));
        }
      }
      return result;
    } catch (SQLException e) {
      return Collections.emptyList();
    }
  }


  // Faz o delete do usuário pelo ID
  @Override
    public void deleteById(long idDelete) {
    String selectSql = "SELECT COUNT(*) FROM students WHERE id = ?;";
    String deleteSql = "DELETE FROM students WHERE id = ?;";
    try (PreparedStatement selectPrst = this.connection.prepareStatement(selectSql)) {
        selectPrst.setLong(1, idDelete);
        try (ResultSet rs = selectPrst.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) {
                // ID encontrado, procede com a exclusão
                try (PreparedStatement deletePrst = this.connection.prepareStatement(deleteSql)) {
                    deletePrst.setLong(1, idDelete);
                    deletePrst.executeUpdate();
                }
            } else {
                // ID não encontrado, lança exceção
                throw new RuntimeException("Estudante com ID " + idDelete + " não encontrado.");
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Erro ao deletar estudante: " + e.getMessage(), e);
    }
}


    //Faz a atualização cadastral
    @Override
    public void updateStudant(long idUpdate, String newName, Integer newAge, String newEmail) {
    // Primeiro, verifica se o ID existe
    String checkSql = "SELECT id FROM students WHERE id = ?";
    try (PreparedStatement checkPrst = this.connection.prepareStatement(checkSql)) {
        checkPrst.setLong(1, idUpdate);
        try (ResultSet resultSet = checkPrst.executeQuery()) {
            // Se não encontrar nenhum resultado, o ID não existe
            if (!resultSet.next()) {
                throw new RuntimeException("Estudante com ID " + idUpdate + " não encontrado.");
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Erro ao verificar estudante: " + e.getMessage(), e);
    }
    // Se o ID exister ele realiza a atualização
    String updateSql = "UPDATE students SET name = ?, age = ?, email = ? WHERE id = ?";
    try (PreparedStatement updatePrst = this.connection.prepareStatement(updateSql)) {
        updatePrst.setString(1, newName);
        updatePrst.setInt(2, newAge);
        updatePrst.setString(3, newEmail);
        updatePrst.setLong(4, idUpdate);
        //rowsAffected verificar se tem linhas válidas para fazer a ação
        //rowsAffected é a variável que armazena o número de linhas afetadas pela execução do comando SQL.
        int rowsAffected = updatePrst.executeUpdate();
        if (rowsAffected == 0) {
            throw new RuntimeException("Nenhuma linha foi atualizada. Verifique se o ID é correto.");
        }
    } catch (SQLException e) {
        throw new RuntimeException("Erro ao atualizar estudante: " + e.getMessage(), e);
    }

}

    // Encontra o usuáiro pelo ID
    @Override
    public Studant findById(long id) {
    String sql = "SELECT id, name, age, email FROM students WHERE id = ?";
    try (PreparedStatement prst = this.connection.prepareStatement(sql)) {
        prst.setLong(1, id);
        try (ResultSet rs = prst.executeQuery()) {
            if (rs.next()) {
                long foundId = rs.getLong("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String email = rs.getString("email");
                return new Studant(foundId, name, age, email);
            } else {
                return null; // Retorna null se o ID não for encontrado
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Erro ao buscar estudante por ID: " + e.getMessage(), e);
    }
}

    // Encontra o usuário pelo nome
    @Override
    public List<Studant> filterByName(String name) {
    String sql = "SELECT id, name, age, email FROM students WHERE name LIKE ?";
    List<Studant> result = new ArrayList<>();
    try (PreparedStatement prst = this.connection.prepareStatement(sql)) {
        prst.setString(1, "%" + name + "%");
        try (ResultSet rs = prst.executeQuery()) {
            while (rs.next()) {
                long id = rs.getLong("id");
                String studentName = rs.getString("name");
                int age = rs.getInt("age");
                String email = rs.getString("email");
                result.add(new Studant(id, studentName, age, email));
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Erro ao buscar estudantes por nome: " + e.getMessage(), e);
    }
    return result;
}
  
}
