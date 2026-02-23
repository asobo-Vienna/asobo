package at.msm.asobo.interfaces;

import java.util.UUID;

public interface PictureEntity {
  UUID getId();

  void setId(UUID id);

  String getPictureURI();

  void setPictureURI(String pictureURI);
}
