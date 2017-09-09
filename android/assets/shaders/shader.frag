
varying vec4 v_color;
varying vec2 v_texCoord0;

uniform sampler2D u_sampler2D;
uniform vec2 u_resolution;
uniform vec2 u_playerPosition;


void main() {

   vec4 color = texture2D(u_sampler2D, v_texCoord0) * v_color;

       vec2 relativePosition = gl_FragCoord.xy / u_resolution - u_playerPosition; //Position of the vignette
       relativePosition.x *= u_resolution.x / u_resolution.y;  //The vignette is a circle
       float len = length(relativePosition);
       float vignette = smoothstep(8.0, 5.0, len);
       color.rgb = mix(color.rgb, color.rgb * vignette, .7);

       gl_FragColor = color;

//    Invert
//    color.rgb = 1. - color.rgb;

//    Red color everywhere
//    gl_FragColor = vec4(1.0, 0.0, 1.0, 1.0);
}
