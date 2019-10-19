package assignment2;

import assignment2.DiskStorage.UserDiskStorageHandler;
import assignment2.exceptions.InvalidUserDetailException;
import assignment2.models.Course;
import assignment2.models.User;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserManager {
    Set<User> users ;

    public UserManager() {
        this.users = (new UserDiskStorageHandler()).getUsersFromDisk() ;
    }

    private int showMainMenu(){
        System.out.println("1.Add User details");
        System.out.println("2.Display User details");
        System.out.println("3.Delete User details");
        System.out.println("4.Save User details");
        System.out.println("5.Exit\n");
        System.out.println("Enter choice : ");
        try{
            return Integer.parseInt((new Scanner(System.in)).nextLine());
        }
        catch (NumberFormatException e){
            return 5 ;
        }
    }

//    Get a new user by showing a menu to add the new user
    private void getNewUser() throws InvalidUserDetailException {
        try{
            users.add(NewUserCreator.getNewUser()) ;
        }
        catch (InvalidUserDetailException e){
            System.out.println("Invalid Details entered : " + e.getMessage());
        }
    }

    private void displayUserDetailsInSortedOrder() throws  InvalidUserDetailException{
        System.out.println("1. Name");
        System.out.println("2. roll number");
        System.out.println("3. age");
        System.out.println("4. address");
        System.out.print("On what field should the users be sorted on (Enter choice) :  ");
        Scanner scan =new Scanner(System.in) ;
        int choice , order ;
        try{
            choice = Integer.parseInt(scan.nextLine()) ;
            System.out.println("1. Ascending order ");
            System.out.println("2. Descending order");
            System.out.print("Enter choice : ");
            order = Integer.parseInt(scan.nextLine()) ;
            if(order<1 || order>2 || choice<1 || choice>4) throw new NumberFormatException() ;
        }
        catch (NumberFormatException e) {
            throw new InvalidUserDetailException("Invalid choice entered ! ");
        }

        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.println("Name\t\t\t\t\tRoll Number\t\t\t\t\tAge\t\t\t\t\tAddress\t\t\t\t\tCourses");
        System.out.println("-------------------------------------------------------------------------------------------------");

        users.stream().sorted((a,b)->{
            int result = 0 ;
            switch (choice){
                case 1 : result = a.getName().compareTo(b.getName()) ; break ;
                case 2 : result = a.getRollNumber()-b.getRollNumber()  ; break ;
                case 3 : result = a.getAge()-b.getAge() ; break ;
                case 4 : result = a.getAddress().compareTo(b.getAddress()) ; break ;
            }
//            If descending order is selected , invert the sign of result
            if(order==2) result = -result ;
            return  result ;
        }).forEach(u->u.display()); ;
    }

//    Returns the user that is deleted else returns null if no such roll number is present
    private User deleteUser(){
        System.out.println("Enter roll number : ");

        int roll ;
        try{
            roll = (new Scanner(System.in)).nextInt() ;
        }
        catch (NumberFormatException e){
            System.out.println("Invalid Roll number ! It must be an integer ");
            return null ;
        }

        final User[] user = {null};
        users.stream().filter(e->e.getRollNumber()==roll).findFirst().ifPresent(u-> user[0] = u) ;
        User userToDelete = user[0] ;
        if(userToDelete!=null) users.remove(userToDelete);
        System.out.println("User : " + userToDelete.getName() + " deleted.");
        return userToDelete ;
    }

    private void saveToDisk(){
        (new UserDiskStorageHandler()).saveUsersToDisk(users) ;
    }

    private void promptForSavingChanges(){
        System.out.println("Do you wish to save changes before exit ? (y/n)");
        String choice = (new Scanner(System.in)).nextLine() ;
        if(choice.equals("y")){
            saveToDisk();
        }
        System.exit(0);
    }


    public void startManager(){
        int choice = showMainMenu();
        while(true){
///        Invalid UserDetailException is handled at every case so that exception only terminates the current operation that the user has selected and not the entire program.
            switch (choice){
                case 1 :
                    getNewUser() ; break ;
                case 2 :
                    try{
                        displayUserDetailsInSortedOrder();
                    }
                    catch (InvalidUserDetailException e) {
                        System.out.println("Invalid entry : " + e.getMessage());
                    }
                    break;
                case 3 : deleteUser() ; break ;
                case 4 : saveToDisk(); break ;
                default : promptForSavingChanges();
            }
            choice = showMainMenu() ;
        }
    }
}
