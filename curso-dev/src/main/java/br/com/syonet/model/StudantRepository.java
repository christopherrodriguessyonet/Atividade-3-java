package br.com.syonet.model;

import java.util.List;

public interface StudantRepository {
  Studant create(Studant studant);
  List<Studant> listAll();
  void deleteById(long id);
  void updateStudant(long id, String newName, Integer newAge, String newEmail);
  Studant findById(long id);
  List<Studant> filterByName(String name);

}
