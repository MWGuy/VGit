package com.mwguy.vgit.api;

public enum GitPackType {
    RECEIVE_PACK("receive-pack", "001f#"), // push
    UPLOAD_PACK("upload-pack", "001e#"); // pull

    private final String type;
    private final String magic;

    GitPackType(String type, String magic) {
        this.type = type;
        this.magic = magic;
    }

    public String getContentType() {
        return String.format("application/x-git-%s-advertisement", type);
    }

    public String getName() {
        return "git-" + type;
    }

    public String getMagic() {
        return this.magic;
    }

    public static GitPackType of(String pack) {
        if ("git-receive-pack".equals(pack)) {
            return GitPackType.RECEIVE_PACK;
        }

        return GitPackType.UPLOAD_PACK;
    }
}
