/*
Esta classe representa um objeto para uma entidade
que será armazenado em uma árvore B+

Neste caso em particular, este objeto é representado
por dois números inteiros para que possa conter
relacionamentos entre dois IDs de entidades quaisquer
 
Implementado pelo Prof. Marcos Kutova
v1.0 - 2021
*/

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParIntLong implements RegistroArvoreBMais<ParIntLong> {

  private int num1;
  private long num2;
  private short TAMANHO = 12;

  public ParIntLong() {
    this(-1, -1);
  }

  public ParIntLong(int n1) {
    this(n1, -1);
  }

  public ParIntLong(int n1, long n2) {
    try {
      this.num1 = n1; // ID do filme
      this.num2 = n2; // Posicao do filme
    } catch (Exception ec) {
      ec.printStackTrace();
    }
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(this.num1); // ou simplesmente: return id;
  }
  public ParIntLong clone() {
    return new ParIntLong(this.num1, this.num2);
  }

  public short size() {
    return this.TAMANHO;
  }

  public int compareTo(ParIntLong a) {
    if (this.num1 != a.num1)
      return this.num1 - a.num1;
    else
      // Só compara os valores de Num2, se o Num2 da busca for diferente de -1
      // Isso é necessário para que seja possível a busca de lista
      return 0;//this.num2 == -1 ? 0 : this.num2 - a.num2;
  }

  public String toString() {
    return String.format("%3d", this.num1) + ";" + String.format("%-3d", this.num2);
  }

  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(this.num1);
    dos.writeLong(this.num2);
    return baos.toByteArray();
  }

  public void fromByteArray(byte[] ba) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(ba);
    DataInputStream dis = new DataInputStream(bais);
    this.num1 = dis.readInt();
    this.num2 = dis.readLong();
  }

  public long getNum2(){
    return this.num2;
  }

}