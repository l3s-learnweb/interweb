package de.l3s.bingService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.l3s.bingService.jsonDeserializers.ImageDeserializer;
import de.l3s.bingService.jsonDeserializers.ImageMainHolderDeserializer;
import de.l3s.bingService.jsonDeserializers.MediaDeserializer;
import de.l3s.bingService.jsonDeserializers.NewsDeserializer;
import de.l3s.bingService.jsonDeserializers.RelatedSearchDeserializer;
import de.l3s.bingService.jsonDeserializers.VideoDeselializer;
import de.l3s.bingService.jsonDeserializers.VideoHolderDeserializer;
import de.l3s.bingService.jsonDeserializers.WebPageDeserializer;
import de.l3s.bingService.jsonDeserializers.WebPageHolderDeserializer;
import de.l3s.bingService.models.BingResponse;
import de.l3s.bingService.models.Image;
import de.l3s.bingService.models.ImageHolder;
import de.l3s.bingService.models.Media;
import de.l3s.bingService.models.New;
import de.l3s.bingService.models.RelatedSearch;
import de.l3s.bingService.models.Video;
import de.l3s.bingService.models.VideoHolder;
import de.l3s.bingService.models.WebPage;
import de.l3s.bingService.models.WebPagesMainHolder;

public class JsonParser
{

    public static BingResponse fromJson(String jsonLine)
    {
        GsonBuilder builder = new GsonBuilder();
        registrateAdapters(builder);
        Gson gson = builder.create();
        return gson.fromJson(jsonLine, BingResponse.class);
    }

    private static void registrateAdapters(GsonBuilder builder)
    {
        builder.registerTypeAdapter(WebPagesMainHolder.class, new WebPageHolderDeserializer());
        builder.registerTypeAdapter(WebPage.class, new WebPageDeserializer());
        builder.registerTypeAdapter(RelatedSearch.class, new RelatedSearchDeserializer());
        builder.registerTypeAdapter(Image.class, new ImageDeserializer());
        builder.registerTypeAdapter(ImageHolder.class, new ImageMainHolderDeserializer());
        builder.registerTypeAdapter(Media.class, new MediaDeserializer());
        builder.registerTypeAdapter(New.class, new NewsDeserializer());
        builder.registerTypeAdapter(RelatedSearch.class, new RelatedSearchDeserializer());
        builder.registerTypeAdapter(Video.class, new VideoDeselializer());
        builder.registerTypeAdapter(VideoHolder.class, new VideoHolderDeserializer());
    }

}
