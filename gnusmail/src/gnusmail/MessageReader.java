package gnusmail;

import gnusmail.core.cnx.Connection;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

public class MessageReader implements Iterable<Message> {
    //Used to keep track of partially unprocessed folders. When a folder
    //is totally processed, it's connection can be closed
    //Map<String, Integer> mailsToReadFromFolders;

    private class ComparableMessage implements Comparable<ComparableMessage> {
        private Message message;
        private Date date;

        public ComparableMessage(Message msg) {
            this.message = msg;
            try {
                this.date = msg.getReceivedDate();
            } catch (MessagingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public Message getMessage() {
            return message;
        }

        @Override
        public int compareTo(ComparableMessage o) {
            return this.date.compareTo(o.date);
        }

        public Folder getFolder() {
            return message.getFolder();
        }
    }

    private class LimitedMessageReaderIterator implements Iterator<Message> {

        TreeSet<ComparableMessage> message_list;
        int numberOfNexts = 0;

        public LimitedMessageReaderIterator(Connection connection, int limit) {
            message_list = new TreeSet<ComparableMessage>();
            long total_msgs = 0;
            Folder[] folders = null;
            try {
                folders = connection.getCarpetas();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            for (Folder folder : folders) {
                if (!folder.getName().contains("INBOX.Sent") && !folder.getName().contains("antiguos")) {
                    try {
                        if (!folder.isOpen()) {
                            folder.open(javax.mail.Folder.READ_ONLY);
                        }
                        System.out.println(folder.getFullName());
                        int msg_count = folder.getMessageCount();
                        total_msgs += msg_count;
                        if (msg_count <= 0) {
                            continue;
                        }
                        int first_msg = msg_count - limit + 1;
                        if (limit > 0 && first_msg < 1) {
                            first_msg = 1;
                        } else if (limit <= 0) {
                            first_msg = 1;
                        }
                        Message msg = folder.getMessage(first_msg);
                        ComparableMessage comparableMsg = new ComparableMessage(msg);
                        message_list.add(comparableMsg);

                    } catch (MessagingException e) {
                        e.printStackTrace();

                    } finally {
                        try {
                            folder.close(false);
                        } catch (MessagingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
            System.out.println("Total: " + total_msgs);
        }

        @Override
        public boolean hasNext() {
            return !message_list.isEmpty();
        }

        @Override
        public Message next() {
            numberOfNexts++;
            System.out.println("---------- Seen messages " + numberOfNexts);
            ComparableMessage comparableMsg = message_list.pollFirst();
            Message msg = comparableMsg.getMessage();
            Folder folder = msg.getFolder();
            int number = msg.getMessageNumber();
            try {
                if (!folder.isOpen()) {
                    folder.open(javax.mail.Folder.READ_ONLY);
                }
                if (number < folder.getMessageCount()) {

                    Message nextMsg = folder.getMessage(number + 1);
                    ComparableMessage nextComparableMsg = new ComparableMessage(nextMsg);
                    message_list.add(nextComparableMsg);

                }
            } catch (MessagingException e) {
                e.printStackTrace();
            } finally {
                try {
                    folder.close(false);
                } catch (MessagingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return msg;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    private Connection connection;
    private int limit;

    public MessageReader(Connection connection, int limit) {
        this.connection = connection;
        this.limit = limit;
    }

    @Override
    public Iterator<Message> iterator() {
        Iterator<Message> iterator;
        iterator = new LimitedMessageReaderIterator(connection, limit);
        return iterator;
    }
}

