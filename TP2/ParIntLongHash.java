/*

Esta classe representa um PAR CHAVE VALOR (PCV) 
para uma entidade Pessoa. Seu objetivo é representar
uma entrada de índice. 

Esse índice será secundário e indireto, baseado no
email de uma pessoa. Ao fazermos a busca por pessoa,
ele retornará o ID dessa pessoa, para que esse ID
possa ser buscado em um índice direto (que não é
apresentado neste projeto)

Um índice direto de ID precisaria ser criado por meio
de outra classe, cujos dados fossem um int para o ID
e um long para o endereço
 
Implementado pelo Prof. Marcos Kutova
v1.0 - 2021
 
*/

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParIntLongHash implements RegistroHashExtensivel<ParIntLongHash> {

  private int id;
  private long pos;
  private short TAMANHO = 12;

  public ParIntLongHash() {
    this(-1, -1);
  }

  public ParIntLongHash(int id) {
    this(id,-1);
  }

  public ParIntLongHash(int id, long pos) {
    try {
      this.id = id; //id 
      this.pos = pos; //posição

    } catch (Exception ec) {
      ec.printStackTrace();
    }
  }

  public long getPos() {
    return this.pos;
  }


  @Override
  public int hashCode() {
    return Integer.hashCode(id); // ou simplesmente: return id;
}

  public short size() {
    return this.TAMANHO;
  }

  public String toString() {
    return this.id + ";" + this.pos;
  }

  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(id);
    dos.writeLong(pos);
    byte[] bs = baos.toByteArray();
    byte[] bs2 = new byte[TAMANHO];
    for (int i = 0; i < TAMANHO; i++)
      bs2[i] = ' ';
    for (int i = 0; i < bs.length && i < TAMANHO; i++)
      bs2[i] = bs[i];
    return bs2;
  }

  public void fromByteArray(byte[] ba) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(ba);
    DataInputStream dis = new DataInputStream(bais);
    this.id = dis.readInt();
    this.pos = dis.readLong();
  }

  public static int hash(int id) {
    return Math.abs(id); // simples e válido
}

}