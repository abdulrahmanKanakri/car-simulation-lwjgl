package textures;

public class TerrainTexturePack {
    private TerrainTexture backGroundTexture;
    private TerrainTexture rTexture;
    private TerrainTexture gTexture;
    private TerrainTexture bTexture;

    public void setBackGroundTexture(int backGroundTexture) {
        this.backGroundTexture.setTextureId(backGroundTexture);
    }

    public void setrTexture(int rTexture) {
        this.rTexture.setTextureId(rTexture);
    }

    public void setgTexture(int gTexture) {
        this.gTexture.setTextureId(gTexture);
    }

    public void setbTexture(int bTexture) {
        this.bTexture.setTextureId(bTexture);
    }

    public TerrainTexturePack(TerrainTexture backGroundTexture, TerrainTexture rTexture, TerrainTexture gTexture, TerrainTexture bTexture) {
        this.backGroundTexture = backGroundTexture;
        this.rTexture = rTexture;
        this.gTexture = gTexture;
        this.bTexture = bTexture;
    }

    public TerrainTexture getBackGroundTexture() {
        return backGroundTexture;
    }

    public TerrainTexture getrTexture() {
        return rTexture;
    }

    public TerrainTexture getgTexture() {
        return gTexture;
    }

    public TerrainTexture getbTexture() {
        return bTexture;
    }
}
