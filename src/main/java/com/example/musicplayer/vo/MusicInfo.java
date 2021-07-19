package com.example.musicplayer.vo;

public class MusicInfo {
    /**
     * 在这里所有的属性都是用public修饰的，所以在以后调用时直接调用就可以了
     * 如果用private修饰是需要构建set和get方法
     */
    private long id;
    private long likemusicId;//收藏时用于保存的原始id
    private String title;//歌名
    private String artist;//艺术家
    private String album;//专辑
    private long albumId;
    private long duration;//时长
    private long size;//大小
    private String url;//路径
    private int isMusic;//是否为音乐

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLikemusicId() {
        return likemusicId;
    }

    public void setLikemusicId(long likemusicId) {
        this.likemusicId = likemusicId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIsMusic() {
        return isMusic;
    }

    public void setIsMusic(int isMusic) {
        this.isMusic = isMusic;
    }

    public String toString() {
        return "MusicInfo{" +
                "id=" + id +
                ", likemusicId='" + likemusicId + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", albumId=" + albumId +
                ", duration=" + duration +
                ", size=" + size +
                ", url='" + url + '\'' +
                ", isMusic=" + isMusic +
                '}';
    }

}
