package interfaces;

@FunctionalInterface
public interface DataExtractor<T> {
  T extract(String data);
}


