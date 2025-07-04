package com.example.moviebooking.MovieManager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.moviebooking.R;
import com.example.moviebooking.dto.Movie;

import java.util.List;

public class MovieCustomAdapter extends BaseAdapter {
    private Context context;
    private List<Movie> movieList;
    private LayoutInflater layoutInflater;

    public MovieCustomAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Object getItem(int position) {
        return movieList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.movie_item_layout, parent, false);
            holder = new ViewHolder();
            holder.imgThumbnail = convertView.findViewById(R.id.imgMovieThumbnail);
            holder.txtTitle = convertView.findViewById(R.id.txtMovieTitle);
            holder.txtGenre = convertView.findViewById(R.id.txtMovieGenre);
            holder.txtDuration = convertView.findViewById(R.id.txtMovieDuration);
            holder.txtRating = convertView.findViewById(R.id.txtMovieRating);
            holder.txtYear = convertView.findViewById(R.id.txtMovieYear);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Movie movie = movieList.get(position);

        // Set movie data
        holder.txtTitle.setText(movie.getTitle());
        holder.txtGenre.setText(getGenreText(movie.getGenres()));
        holder.txtDuration.setText(movie.getDetailDuration());
        holder.txtRating.setText(movie.getRate());
        holder.txtYear.setText(movie.getYear());

        // Load thumbnail image using Glide
        if (movie.getThumbnail() != null && !movie.getThumbnail().isEmpty()) {
            Glide.with(context)
                    .load(movie.getThumbnail())
                    .apply(new RequestOptions()
                            .transform(new CenterCrop(), new RoundedCorners(16)))
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.imgThumbnail);
        } else {
            holder.imgThumbnail.setImageResource(R.drawable.ic_launcher_background);
        }

        return convertView;
    }

    private String getGenreText(List<String> genres) {
        if (genres == null || genres.isEmpty()) {
            return "Unknown";
        }

        StringBuilder genreText = new StringBuilder();
        for (int i = 0; i < Math.min(2, genres.size()); i++) {
            if (i > 0) {
                genreText.append(", ");
            }
            genreText.append(genres.get(i));
        }

        if (genres.size() > 2) {
            genreText.append("...");
        }

        return genreText.toString();
    }

    static class ViewHolder {
        ImageView imgThumbnail;
        TextView txtTitle;
        TextView txtGenre;
        TextView txtDuration;
        TextView txtRating;
        TextView txtYear;
    }
}
