package gnusmail.datasource;

public abstract class DataSource {
  public static int EMAIL_FROM_IMAPSERVER = 0;
  public static int EMAIL_FROM_FILESYSTEM = 1;
  
  
  public abstract DocumentReader getDocumentReader();
}
