package alluxio.shell.command;

import alluxio.Configuration;
import alluxio.PropertyKey;
import alluxio.client.file.FileSystem;
import alluxio.client.file.FileSystemContext;
import alluxio.client.file.FileSystemMasterClient;

import org.apache.commons.cli.CommandLine;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Prints the relative information of the Alluxio masters.
 */
@ThreadSafe
public final class MasterInfoCommand extends AbstractShellCommand {
  public MasterInfoCommand(FileSystem fs) {
    super(fs);
  }

  @Override
  public String getCommandName() {
    return "masterInfo";
  }

  @Override
  public int getNumOfArgs() {
    return 0;
  }

  @Override
  public void run(CommandLine cl) {
    FileSystemMasterClient client = FileSystemContext.INSTANCE.acquireMasterClient();
    try {
      if (Configuration.getBoolean(PropertyKey.ZOOKEEPER_ENABLED)) {
        runWithFaultTolerance(client);
      } else {
        runWithoutFaultTolerance(client);
      }
    } finally {
      FileSystemContext.INSTANCE.releaseMasterClient(client);
    }
  }

  private void runWithoutFaultTolerance(FileSystemMasterClient client) {
    System.out.println("Alluxio cluster without fault tolerance now");
    printLeader(client);
  }

  private void runWithFaultTolerance(FileSystemMasterClient client) {
    System.out.println("Alluxio cluster with fault tolerance now");
    printLeader(client);
    System.out.println(String.format("All masters: %s", client.getMasterAddresses()));
    System.out.println(String.format("Zookeeper address: %s",
        Configuration.get(PropertyKey.ZOOKEEPER_ADDRESS)));
  }

  private void printLeader(FileSystemMasterClient client) {
    String hostName = client.getAddress().getHostName();
    if (hostName != null) {
      System.out.println(String.format("Current leader master: %s", hostName));
    } else {
      System.out.println("Failed to get the current leader master.");
    }
  }

  @Override
  public String getUsage() {
    return "masterInfo";
  }

  @Override
  public String getDescription() {
    return "Prints the relative information of the Alluxio masters";
  }
}
