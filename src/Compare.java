import java.io.File;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Random;
import it.unisa.dia.gas.jpbc.*;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pbc.curve.PBCTypeACurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.pbc.curve.PBCTypeDCurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.pbc.curve.PBCTypeECurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;


public class Compare{
    String rootDirectory;

    public Compare(String rootDirectory){
        this.rootDirectory = rootDirectory;
    }

    public Compare(){
    }

    public static void main(String[] args) throws Exception {
        Compare instance = new Compare();
        String rootDirectory = "C:\\Users\\Aristotle\\Desktop\\MFA\\src\\Data";
        String[] fileList = new File(rootDirectory).list();



        long startTime;
        long endTime;

        KeyPair keyPair = generateKeyPair();

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        RSAPublicKey rsaPublicKey = (RSAPublicKey) (publicKey);
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)(privateKey);

        BigInteger publicKeyModulus = rsaPublicKey.getModulus();
        BigInteger privateD = rsaPrivateKey.getPrivateExponent();


        //------------------------------------
        PairingParametersGenerator parametersGenerator = new TypeACurveGenerator(90,160) ;
        PairingParameters parameters = parametersGenerator.generate();
        Pairing pairing = PairingFactory.getPairing(parameters);
        PairingFactory.getInstance().setUsePBCWhenPossible(true);

        Element u1 = pairing.getG1().newRandomElement().getImmutable();
        ElementPowPreProcessing gPre = u1.getElementPowPreProcessing();
        BigInteger x =  BigInteger.valueOf((int)(Math.random() *100000));

        //---------------------------------------

        startTime = System.nanoTime();

        for (String f: fileList) {
            byte[] data;
            data = FileHelper.getContentFromFile(rootDirectory + "\\"+f);
            instance.pdp(data,publicKeyModulus, privateD);

        }
        endTime = System.nanoTime();
        System.out.println(endTime-startTime);
//
//        // -----------------------------------------
//
        startTime = System.nanoTime();

        for (String f: fileList) {
            byte[] data;
            data = FileHelper.getContentFromFile(rootDirectory + "\\"+f);

            instance.WangPublic(data, gPre, x);

        }
        endTime = System.nanoTime();
        System.out.println(endTime-startTime);

        // --------------------------------

        parametersGenerator = new TypeACurveGenerator(90,160) ;
        parameters = parametersGenerator.generate();
        pairing = PairingFactory.getPairing(parameters);
        PairingFactory.getInstance().setUsePBCWhenPossible(true);

        BigInteger q = pairing.getG1().getOrder();
        Element x_w = pairing.getZr().newRandomElement();
        Element r = pairing.getZr().newRandomElement();

        MessageDigest md = HashTree.generateMD();
        int ID = 123;
        byte[] hashID = md.digest(intToByteArray(ID));
        BigInteger a = r.add(x_w.mul(new BigInteger(hashID))).toBigInteger().mod(q);

        Element u = pairing.getG1().newRandomElement();
        ElementPowPreProcessing uPre = u.getElementPowPreProcessing();

        startTime = System.nanoTime();

        for (String f: fileList) {
            byte[] data;
            data = FileHelper.getContentFromFile(rootDirectory + "\\"+f);

            instance.IdentityBased(data, pairing, a, uPre);

        }
        endTime = System.nanoTime();
        System.out.println(endTime-startTime);


        // ------------------------------------------


        KeyPair keyPair_Z = generateKeyPair();

        PublicKey publicKey_Z = keyPair_Z.getPublic();
        PrivateKey privateKey_Z = keyPair_Z.getPrivate();

        RSAPublicKey rsaPublicKey_Z = (RSAPublicKey) (publicKey_Z);
        RSAPrivateKey rsaPrivateKey_Z = (RSAPrivateKey)(privateKey_Z);

        BigInteger publicKeyModulus_Z = rsaPublicKey_Z.getModulus();
        BigInteger privateD_Z = rsaPrivateKey_Z.getPrivateExponent();



        startTime = System.nanoTime();

        for (String f: fileList) {
            byte[] data;
            data = FileHelper.getContentFromFile(rootDirectory + "\\"+f);

            instance.Trans2021z(data, publicKeyModulus_Z);

        }
        endTime = System.nanoTime();
        System.out.println(endTime-startTime);



    }
    public void pdp(byte[] data, BigInteger publicKeyModulus, BigInteger privateD) throws Exception{

        MessageDigest md = HashTree.generateMD();
        int w = 123;

        int s = 1;
        int fileLength = data.length;
        int fileSectorSize = fileLength/s;

        for (int i = 0; i < s; i++) {
            byte[] dataPiece = new byte[fileSectorSize];
            System.arraycopy(data, i * fileSectorSize, dataPiece, 0, fileSectorSize);
            BigInteger dataP = new BigInteger(dataPiece);
            dataP = dataP.mod(publicKeyModulus);
            BigInteger hashW = new BigInteger(md.digest(BigInteger.valueOf(w).toByteArray()));
            BigInteger g =  new BigInteger("3");
            BigInteger r0 = g.modPow(dataP, publicKeyModulus);
            BigInteger dTAG = hashW.multiply(r0);
            BigInteger tag = dTAG.modPow(privateD, publicKeyModulus);

        }

        //System.out.println(tag);

    }
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        return generator.generateKeyPair();

    }


    public void WangPublic(byte[] data, ElementPowPreProcessing gPre, BigInteger x){
        int s = 10;int index = 123;
        int fileLength = data.length;
        int fileSectorSize = fileLength/s;
        MessageDigest md = HashTree.generateMD();


        for (int i = 0; i < s; i++) {
            byte[] dataPiece = new byte[fileSectorSize];
            System.arraycopy(data, i*fileSectorSize, dataPiece, 0, fileSectorSize);
            BigInteger dataP = new BigInteger(dataPiece);

            byte[] hash_i = md.digest(BigInteger.valueOf(index).toByteArray());
            Element tag = gPre.pow(dataP).mul(new BigInteger(hash_i)).pow(x);

            
        }

        //System.out.println(tag);
        
        

    }
//
    public void IdentityBased(byte[] data, Pairing pairing, BigInteger a, ElementPowPreProcessing uPre){
        int s = 10;

        MessageDigest md = HashTree.generateMD();
        byte[] hash = md.digest(intToByteArray(123));

        int fileLength = data.length;
        int fileSectorSize = fileLength/s;

        Element temp =  pairing.getG1().newElement(1);

        for (int i = 0; i < s; i++) {
            byte[] dataPiece = new byte[fileSectorSize];
            System.arraycopy(data, i*fileSectorSize, dataPiece, 0, fileSectorSize);
            BigInteger dataP = new BigInteger(dataPiece);

            Element r0 = uPre.pow(dataP);
            temp = temp.mul(r0);
        }
        temp.mul(new BigInteger(hash));
        temp.pow(a);

    }
//
    public void Trans2021z(byte[] data, BigInteger publicKeyModulus){

        int s = 1000;
        int fileLength = data.length;
        int fileSectorSize = fileLength/s;

        for (int i = 0; i < s; i++) {
            byte[] dataPiece = new byte[fileSectorSize];
            System.arraycopy(data, i * fileSectorSize, dataPiece, 0, fileSectorSize);
            BigInteger dataP = new BigInteger(dataPiece);

            BigInteger g =  new BigInteger("3");
            BigInteger tag = g.modPow(dataP, publicKeyModulus);
        }


    }


    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }

//    public BigInteger[] dataDevide(byte[] data, int s){
//
//
//    }

}