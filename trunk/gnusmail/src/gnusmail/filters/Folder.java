package gnusmail.filters;

public final class Folder extends Filter {

	@Override
	public String getValueForHeader(String header) {
		String folder = mess.getFolderAsString();
		return folder;
	}
}
