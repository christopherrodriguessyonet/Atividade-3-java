package br.com.syonet.view;

import java.util.List;
import java.util.Scanner;

import org.postgresql.ssl.SingleCertValidatingFactory;

import br.com.syonet.model.Studant;
import br.com.syonet.service.StudantService;

public class StudantView {

  private int selectedOption;
  private boolean exit;
  private Scanner scanner;

  private StudantService service;

  public StudantView(StudantService service, Scanner scanner) {
    this.service = service;
    this.scanner = scanner;
  }

  public void init() {
    System.out.println("Ola seja bem vindo ao nosso cadastro de estudantes.");
  }

  public void showOptions() {
    System.out.println("Por favor selecione uma das operações abaixo:");
    System.out.println();
    System.out.println("\t(1) - criar novo estudantes");
    System.out.println("\t(2) - Listar estudantes");
    System.out.println("\t(3) - Excluir um estudante");
    System.out.println("\t(4) - Atualizar um estudante");
    System.out.println("\t(5) - Localize um estudante pelo ID");
    System.out.println("\t(6) - Localize um estudante pelo NOME");
    System.out.println("\t(0) - sair");
  }

  public Integer getSelectedOption() {
    return selectedOption;
  }

  public boolean isExit() {
    return this.exit;
  }

  public void readSelectedOption() {
    String nextLine = this.scanner.nextLine();
    int answer = Integer.parseInt(nextLine);
    this.exit = answer == 0;
    this.selectedOption = answer;
  }

  public void executeSelectedOperation() {
    switch (this.selectedOption) {
      case 1:
        this.initCreationProcess();
        break;
      case 2:
        this.initListProcess();
        break;
      case 3:
        this.initDeletProcess();
        break;
      case 4:
        this.initUpdateProcess();
        break;
      case 5:
        this.initLocateProcess();
      break;
      case 6:
        this.initFilterProcess();
      break;
      default:
      System.out.println("*-----Obrigado por participar da turma-----*");
        break;
    }
  }

  private void initListProcess() {
    List<Studant> studants = this.service.listAll();
    if (studants != null && !studants.isEmpty()) {
      System.out.println();
      System.out.println("\t\tid\t\t|\t\tnome\t\t|\t\tidade\t\t|\t\temail");
      for (int i = 0; i < studants.size(); i++) {
        Studant studant = studants.get(i);
        System.out.println("\t\t%d\t\t\t\t%s\t\t\t\t%d\t\t\t\t%s".formatted(
          studant.getId(),
          studant.getName(),
          studant.getAge(),
          studant.getEmail()));
      }
      System.out.println();
    } else {
      System.out.println("Não há estudantes cadastrados!");
    }
  }

  private void initCreationProcess() {
    System.out.println("Ok, qual é o nome do estudante?");
    String name = this.scanner.nextLine();
    System.out.println("E o email do rapaz ou da moça?");
    String email = this.scanner.nextLine();
    System.out.println("Muito bom! agora qual a idade dela ou dele?");
    Integer idade = Integer.parseInt(this.scanner.nextLine());
    System.out.println("Obrigado temos todas as info, criando novo estudante!");
    Studant studant = new Studant(name, idade, email);
    long id = this.service.save(studant);
    System.out.println("O id do novo estudante é " + id);
  }

  private void initDeletProcess() {
    long idDelete = -1;
    boolean valid = false;
    
    while (!valid) {
        try {
            System.out.println("Insira um ID de um estudante para deletar ele da turma:");
            idDelete = Long.parseLong(this.scanner.nextLine());
            valid = true;
        } catch (NumberFormatException e) {
            System.out.println("ID inválido. Por favor, insira um número válido.");
        }
    }
    try {
        this.service.deleteById(idDelete);
        System.out.println("Estudante com ID " + idDelete + " foi deletado da turma.");
    } catch (RuntimeException e) {
        System.out.println(e.getMessage());
    }
}

private void initUpdateProcess() {
  long idUpdate = -1;
  boolean valid = false;

  while (!valid) {
      try {
          System.out.println("Para atualizar um cadastro, insira um ID:");
          idUpdate = Long.parseLong(this.scanner.nextLine());
          valid = true;
      } catch (NumberFormatException e) {
          System.out.println("ID inválido. Por favor, insira um número válido.");
      }
  }

  // Tenta verificar e atualizar o estudante e para o fluxo se o ID não for encontrado
  try {
      this.service.updateStudant(idUpdate, "", 0, ""); // Apenas verifica a existência do ID
  } catch (RuntimeException e) {
      // Exibe mensagem de erro se o ID não for encontrado e para o processo
      System.out.println(e.getMessage());
      return; // Sai do método e interrompe o processo
  }

  // Solicita novos detalhes do estudante somente se o ID for válido
  try {
      System.out.println("Insira o novo nome do estudante:");
      String newName = this.scanner.nextLine();
      System.out.println("Insira a nova idade do estudante:");
      Integer newAge = Integer.parseInt(this.scanner.nextLine());
      System.out.println("Insira o novo email do estudante:");
      String newEmail = this.scanner.nextLine();

      // Atualiza o estudante com os novos dados
      this.service.updateStudant(idUpdate, newName, newAge, newEmail);
      System.out.println("Estudante com ID " + idUpdate + " atualizado com sucesso.");
  } catch (RuntimeException e) {
      // Exibe mensagem de erro se ocorrer algum problema na atualização
      System.out.println(e.getMessage());
  }
}

private void initLocateProcess() {
  long idLocate = -1;
  boolean valid = false;

  // Verifica se o ID é válido
  while (!valid) {
      try {
          System.out.println("Digite um ID para localizar o aluno:");
          idLocate = Long.parseLong(this.scanner.nextLine());
          valid = true;
      } catch (NumberFormatException e) {
          System.out.println("ID inválido. Por favor, insira um número válido.");
      }
  }
  // Localiza o aluno com o ID fornecido
  try {
      Studant studant = this.service.findById(idLocate);
      if (studant != null) {
          // Exibe as informações do aluno
          System.out.println();
          System.out.println("\t\tid\t\t|\t\tnome\t\t|\t\tidade\t\t|\t\temail");
          System.out.println("\t\t%d\t\t\t\t%s\t\t\t\t%d\t\t\t\t%s".formatted(
              studant.getId(),
              studant.getName(),
              studant.getAge(),
              studant.getEmail()));
          System.out.println();
      } else {
          System.out.println("Nenhum aluno encontrado com ID " + idLocate);
      }
  } catch (RuntimeException e) {
      System.out.println(e.getMessage());
  }
}

private void initFilterProcess() {
  System.out.println("Digite o nome do aluno para filtrar:");
  String nameFilter = this.scanner.nextLine();

  List<Studant> studants = this.service.filterByName(nameFilter);
  if (studants != null && !studants.isEmpty()) {
      System.out.println();
      System.out.println("\t\tid\t\t|\t\tnome\t\t|\t\tidade\t\t|\t\temail");
      for (Studant studant : studants) {
          System.out.println("\t\t%d\t\t\t\t%s\t\t\t\t%d\t\t\t\t%s".formatted(
              studant.getId(),
              studant.getName(),
              studant.getAge(),
              studant.getEmail()));
      }
      System.out.println();
  } else {
      System.out.println("Nenhum aluno encontrado com o nome fornecido.");
  }
}



}
